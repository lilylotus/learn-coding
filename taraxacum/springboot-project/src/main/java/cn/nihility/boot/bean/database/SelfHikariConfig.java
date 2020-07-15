package cn.nihility.boot.bean.database;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * 自定义的 Hikari 连接池配置
 *
 * @author clover
 * @date 2020-01-08 15:03
 */
@Component
@PropertySource(value = {"classpath:properties/database-pool.properties"}, encoding = "UTF-8")
@Setter
@Getter
public class SelfHikariConfig {

    @Value("${mysql.driverClass}")
    private String driverClassName;
    @Value("${mysql.url}")
    private String jdbcUrl;
    @Value("${mysql.user}")
    private String username;
    @Value("${mysql.password}")
    private String password;
    @Value("${maxPoolSize}")
    private int maxPoolSize;
    @Value("${maxLifetime}")
    private long maxLifetime;
    @Value("${connectionTimeout}")
    private long connectionTimeout;
    @Value("${idleTimeout}")
    private long idleTimeout;
    @Value("${minIdle}")
    private int minIdle;

}
