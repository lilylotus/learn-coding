package cn.nihilty.gateway.refresh;

import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.cloud.nacos.NacosConfigProperties;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.cloud.gateway.config.GatewayProperties;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.SmartLifecycle;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

public class DynamicGatewayRefresh implements EnvironmentAware, SmartLifecycle, ApplicationEventPublisherAware, BeanFactoryAware {

    private static final Logger log = LoggerFactory.getLogger(DynamicGatewayRefresh.class);

    public static final String DYNAMIC_GATEWAY_CONFIG_NAME = "local.gateway.dynamic.name";
    public static final String DYNAMIC_GATEWAY_DEFAULT_CONFIG_NAME = "dynamic-gateway.yml";

    private static final Bindable<GatewayProperties> GATEWAY_PROPERTIES = Bindable.of(GatewayProperties.class);
    private static final Bindable<NacosConfigProperties> NACOS_CONFIG_PROPERTIES = Bindable.of(NacosConfigProperties.class);

    private final AtomicBoolean status = new AtomicBoolean(false);

    /**
     * spring Aware 注入变量
     */
    private Environment environment;
    /**
     * spring Aware 注入变量
     */
    private BeanFactory beanFactory;
    /**
     * spring Aware 注入变量
     */
    private ApplicationEventPublisher applicationEventPublisher;
    /**
     * spring boot 启动调用 start() 方法创建对象
     */
    private ConfigService configService;

    private final GatewayProperties gatewayProperties;

    public DynamicGatewayRefresh(GatewayProperties gatewayProperties) {
        this.gatewayProperties = gatewayProperties;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void start() {
        log.info("监听 gateway 配置文件动态变更启动");
        if (status.get()) {
            return;
        }

        final String listenFileName = environment.getProperty(DYNAMIC_GATEWAY_CONFIG_NAME,
                DYNAMIC_GATEWAY_DEFAULT_CONFIG_NAME);
        log.info("动态加载 gateway 文件 [{}]", listenFileName);

        Boolean enabled = environment.getProperty("spring.cloud.nacos.config.enabled", Boolean.class, Boolean.TRUE);

        if (Boolean.TRUE.equals(enabled)) {
            NacosConfigManager nacosConfigManager = beanFactory.getBean("nacosConfigManager", NacosConfigManager.class);
            NacosConfigProperties configProperties = nacosConfigManager.getNacosConfigProperties();
            ConfigService localConfigService = nacosConfigManager.getConfigService();
            GatewayRouteRefreshListener listener = new GatewayRouteRefreshListener(listenFileName);
            try {
                localConfigService.addListener(listenFileName, configProperties.getGroup(), listener);
                status.compareAndSet(false, true);
                log.info("成功启动 gateway 动态配置监听服务");

                initialPullConfigAndRefresh(listenFileName, configProperties, localConfigService, listener);
            } catch (NacosException e) {
                log.error("创建/添加监听动态加载 gateway 配置文件 [{}] 实例异常", listenFileName, e);
                status.compareAndSet(true, false);
            }
        } else {
            configRefreshListening(listenFileName);
        }

    }

    /**
     * gateway 动态配置文件更新后动态刷新 gateway 配置监听
     *
     * @param listenFileName 动态监听的配置文件
     */
    private void configRefreshListening(String listenFileName) {
        NacosConfigProperties properties = Binder.get(environment)
                .bind(NacosConfigProperties.PREFIX, NACOS_CONFIG_PROPERTIES).get();

        properties.setEnvironment(environment);
        properties.setName(listenFileName);
        properties.setFileExtension(PropertySourceParseHandler.getFileExtension(listenFileName));

        GatewayRouteRefreshListener listener = new GatewayRouteRefreshListener(listenFileName);
        try {
            configService = NacosFactory.createConfigService(properties.assembleConfigServiceProperties());
            configService.addListener(listenFileName, properties.getGroup(), listener);
            status.compareAndSet(false, true);
            log.info("成功启动 gateway 动态配置监听服务");
        } catch (NacosException e) {
            log.error("创建监听动态加载 gateway 配置文件 [{}] 实例异常", listenFileName, e);
            status.compareAndSet(true, false);
        }

        initialPullConfigAndRefresh(listenFileName, properties, configService, listener);
    }

    private void initialPullConfigAndRefresh(String listenFileName, NacosConfigProperties properties,
                                             ConfigService configService, GatewayRouteRefreshListener listener) {
        try {
            if (configService != null) {
                listener.refresh(configService.getConfig(listenFileName, properties.getGroup(), properties.getTimeout()));
            }
        } catch (NacosException e) {
            log.error("首次加载 gateway 动态配置 [{}] 失败", listenFileName, e);
        }
    }

    @Override
    public void stop() {
        log.info("监听 gateway 配置文件动态变更关闭");
        if (status.get() && configService != null) {
            try {
                configService.shutDown();
                status.compareAndSet(true, false);
            } catch (NacosException e) {
                log.error("关闭监听动态加载 gateway 配置文件异常", e);
            }
        }
    }

    @Override
    public boolean isRunning() {
        return status.get();
    }

    private class GatewayRouteRefreshListener implements Listener {

        private final String fileName;

        public GatewayRouteRefreshListener(String fileName) {
            this.fileName = fileName;
        }

        @Override
        public Executor getExecutor() {
            return null;
        }

        @Override
        public void receiveConfigInfo(String configInfo) {
            if (log.isDebugEnabled()) {
                log.debug("监听 gateway 配置文件动态变更，刷新路由 [{}]", configInfo);
            }
            refresh(configInfo);
        }

        public void refresh(String configInfo) {
            GatewayProperties refreshGatewayProperties = null;
            if (!StringUtils.hasText(configInfo)) {
                return;
            }
            try {
                List<PropertySource<?>> propertySourceList =
                        PropertySourceParseHandler.parsePropertyResource(fileName, configInfo);
                StandardEnvironment standardEnvironment = new StandardEnvironment();
                MutablePropertySources mutablePropertySources = standardEnvironment.getPropertySources();
                propertySourceList.forEach(mutablePropertySources::addLast);
                refreshGatewayProperties = Binder.get(standardEnvironment)
                        .bind(GatewayProperties.PREFIX, GATEWAY_PROPERTIES).get();
                if (log.isDebugEnabled()) {
                    log.debug("Refresh Gateway Properties");
                }
            } catch (IOException e) {
                log.error("解析 gateway 配置变更文件内容 [{}] 异常", configInfo, e);
            }

            if (refreshGatewayProperties != null) {
                dynamicRefreshGatewayRoute(refreshGatewayProperties);
            }
        }

        private void dynamicRefreshGatewayRoute(GatewayProperties refreshGatewayProperties) {
            List<RouteDefinition> refreshRoutes = refreshGatewayProperties.getRoutes();
            if (!refreshRoutes.isEmpty()) {
                gatewayProperties.setRoutes(refreshRoutes);
                applicationEventPublisher.publishEvent(new RefreshRoutesEvent(this));
            }
        }

        private void dynamicRefreshGatewayRoute2(GatewayProperties refreshGatewayProperties) {
            List<RouteDefinition> refreshRoutes = refreshGatewayProperties.getRoutes();
            List<RouteDefinition> gatewayRoutes = gatewayProperties.getRoutes();
            List<RouteDefinition> newGatewayRoutes = new ArrayList<>(16);

            Set<RouteDefinition> refreshRoutesSet = new LinkedHashSet<>(refreshRoutes);

            if (!refreshRoutes.isEmpty()) {
                // 目前有个 bug，无法删除创建的路由策略
                for (RouteDefinition route : gatewayRoutes) {
                    RouteDefinition definition = refreshRoutes.stream()
                            .filter(r -> r.getId().equals(route.getId())).findFirst().orElse(null);
                    // 刷新的路由配置中不存在时，直接添加刷新的路由配置
                    if (null == definition) {
                        newGatewayRoutes.add(route);
                    } else if (route.equals(definition)) {
                        // 原有的路由配置没有修改，采用原有的路由配置
                        newGatewayRoutes.add(route);
                        refreshRoutesSet.remove(route);
                    } else {
                        // 原有的路由配置已存在，但是修改了，采用刷新后的路由配置
                        newGatewayRoutes.add(definition);
                        refreshRoutesSet.remove(definition);
                    }
                }
                newGatewayRoutes.addAll(refreshRoutesSet);

                gatewayProperties.setRoutes(newGatewayRoutes);
                applicationEventPublisher.publishEvent(new RefreshRoutesEvent(this));
            }

        }
    }
}
