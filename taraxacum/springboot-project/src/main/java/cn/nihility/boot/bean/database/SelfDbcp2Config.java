package cn.nihility.boot.bean.database;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * 自定义的 DBCP2 连接池配置
 *
 * @author clover
 * @date 2020-01-08 15:03
 */
@Component
@PropertySource(value = {"classpath:properties/database-pool.properties"}, encoding = "UTF-8")
@Setter
@Getter
public class SelfDbcp2Config {

    @Value("${mysql.driverClass}")
    private String driverClassName;
    @Value("${mysql.url}")
    private String jdbcUrl;
    @Value("${mysql.user}")
    private String username;
    @Value("${mysql.password}")
    private String password;

    @Value("${initialSize}")
    private int initialSize;
    @Value("${maxTotal}")
    private int maxTotal;
    @Value("${maxIdle}")
    private int maxIdle;
    @Value("${minIdle}")
    private int minIdle;
    @Value("${maxWaitMillis}")
    private int maxWaitMillis;
    @Value("${timeBetweenEvictionRunsMillis}")
    private int timeBetweenEvictionRunsMillis;
    @Value("${minEvictableIdleTimeMillis}")
    private int minEvictableIdleTimeMillis;
    @Value("${testOnBorrow}")
    private boolean testOnBorrow;
    @Value("${testOnReturn}")
    private boolean testOnReturn;
    @Value("${numTestsPerEvictionRun}")
    private int numTestsPerEvictionRun;
    @Value("${testWhileIdle}")
    private boolean testWhileIdle;

}
