package cn.nihility.boot.config;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class ApplicationAwareConfiguration implements ApplicationContextAware {

    private static final Logger log = LoggerFactory.getLogger(ApplicationAwareConfiguration.class);
    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        log.info("Register Application Context to ApplicationAwareConfiguration");
        ApplicationAwareConfiguration.applicationContext = applicationContext;
    }

    public static <T> T getBeanByClass(Class<T> clazz) {
        if (null == clazz) {
            throw new IllegalArgumentException("arg {clazz} can't be null.");
        }
        log.info("query [{}] from application context ioc by type", clazz.getName());
        return applicationContext.getBean(clazz);
    }

    public static Object getBeanByName(String beanName) {
        if (StringUtils.isBlank(beanName)) {
            throw new IllegalArgumentException("argument beanName cannot be null");
        }
        log.info("query [{}] from application context ioc by name", beanName);
        return applicationContext.getBean(beanName);
    }
}
