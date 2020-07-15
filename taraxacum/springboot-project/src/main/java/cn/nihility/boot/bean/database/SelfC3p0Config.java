package cn.nihility.boot.bean.database;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * 自定义的 C3P0 连接池配置
 *
 * @author clover
 * @date 2020-01-08 15:03
 */
@Component
@PropertySource(value = {"classpath:properties/database-pool.properties"}, encoding = "UTF-8")
@Setter
@Getter
public class SelfC3p0Config {

    @Value("${mysql.driverClass}")
    private String driverClassName;
    @Value("${mysql.url}")
    private String jdbcUrl;
    @Value("${mysql.user}")
    private String username;
    @Value("${mysql.password}")
    private String password;

    @Value("${initialPoolSize}")
    private int initialPoolSize;
    @Value("${minPoolSize}")
    private int minPoolSize;
    @Value("${maxPoolSize}")
    private int maxPoolSize;
    @Value("${acquireIncrement}")
    private int acquireIncrement;
    @Value("${checkoutTimeout}")
    private int checkoutTimeout;
    @Value("${idleConnectionTestPeriod}")
    private int idleConnectionTestPeriod;
    @Value("${maxIdleTime}")
    private int maxIdleTime;
    @Value("${testConnectionOnCheckout}")
    private boolean testConnectionOnCheckout;
    @Value("${testConnectionOnCheckin}")
    private boolean testConnectionOnCheckin;
    @Value("${numHelperThreads}")
    private int numHelperThreads;

}
