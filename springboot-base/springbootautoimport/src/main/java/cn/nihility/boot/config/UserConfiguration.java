package cn.nihility.boot.config;

import cn.nihility.boot.bean.User;
import org.springframework.context.annotation.Bean;

/**
 * 没有 Spring 的注解
 *
 * @author dandelion
 * @date 2020-04-25 12:54
 */
public class UserConfiguration {

    @Bean
    public User getUser() {
        return new User("自动装载机制", 20);
    }

}
