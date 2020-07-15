package cn.nihility.boot.bean;

import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * 使用 properties 配置引入值
 *
 * @author clover
 * @date 2020-01-08 14:13
 */
@Component
@Setter
@Getter
@ToString
@PropertySource(encoding = "UTF-8", value = {"classpath:properties/inject.properties"})
public class InjectBeanDemo2 {

    @Value("${name}")
    private String name;
    @Value("${age}")
    private Integer age;

    @Bean
    @ConfigurationProperties(prefix = "property")
    public InnerClass innerClass() {
        return new InnerClass();
    }

    @Bean
    public InnerPropertyClass innerPropertyClass(@Value("${name}") String name,
                                                 @Value("${age}") Integer age) {
        return new InnerPropertyClass(name, age);
    }

    @Setter
    @Getter
    @ToString
    public static class InnerClass {
        private String name;
        private Integer age;
    }

    @Setter
    @Getter
    @ToString
    @AllArgsConstructor
    @NoArgsConstructor
    public static class InnerPropertyClass {
        private String name;
        private Integer age;
    }

}
