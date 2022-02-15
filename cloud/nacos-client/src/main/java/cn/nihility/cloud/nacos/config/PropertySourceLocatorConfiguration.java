package cn.nihility.cloud.nacos.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.bootstrap.config.PropertySourceLocator;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author nihility
 */
public class PropertySourceLocatorConfiguration implements PropertySourceLocator {

    private static final Logger log = LoggerFactory.getLogger(PropertySourceLocatorConfiguration.class);

    @Override
    public PropertySource<?> locate(Environment environment) {
        log.info("自定义配置文件加载");
        CompositePropertySource composite = new CompositePropertySource("SELF_CUSTOMIZE");
        Map<String, Object> source = new LinkedHashMap<>();
        source.put("self.customize.key", "自定义配置文件加载");
        source.put("server.port", "30032");
        composite.addFirstPropertySource(new MapPropertySource("SELF_CUSTOMIZE_MAP", source));
        return composite;
    }

}
