package cn.nihility.boot.config;

import cn.nihility.boot.bean.database.SelfC3p0Config;
import cn.nihility.boot.bean.database.SelfDbcp2Config;
import cn.nihility.boot.bean.database.SelfDruidConfig;
import cn.nihility.boot.bean.database.SelfHikariConfig;
import com.alibaba.druid.pool.DruidDataSource;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.beans.PropertyVetoException;

/**
 * @Autowired  --> 四种模式 ： byName、byType、constructor、autodectect
 * @Qualifier(value = "basicDataSource") 没有作用,不能够按照指定的名称注入 Bean
 *  执行顺序：1. 先查找 Autowired 指定类型的 Bean
 *          2. 若没有找到 Bean 则会报异常
 *          3. 找到一个指定类型的 Bean 则会自动匹配，并把 Bean 装配到要 Inject 的字段当中
 *          4. 若有多个 Bean 则按照注入字段的名称匹配注入 Bean 值，匹配成功后装配到指定字段当中。
 *
 * @Bean 注解放在方法上时，Bean 的名称为方法名称
 * @author dandelion
 * @date 2020:06:26 23:43
 */
@Configuration
@PropertySource(encoding = "UTF-8", value = {"classpath:properties/datasource02.properties"})
public class DatasourceConfigurationBean {

    /* ========== Hikari ========== */
    @Bean(destroyMethod = "close")
    @Profile("hikari")
    public HikariDataSource hikariDataSource(SelfHikariConfig config) {
        HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setDriverClassName(config.getDriverClassName());
        hikariDataSource.setJdbcUrl(config.getJdbcUrl());
        hikariDataSource.setUsername(config.getUsername());
        hikariDataSource.setPassword(config.getPassword());
        hikariDataSource.setMaxLifetime(config.getMaxLifetime());
        hikariDataSource.setMaximumPoolSize(config.getMaxPoolSize());
        hikariDataSource.setMinimumIdle(config.getMinIdle());
        hikariDataSource.setIdleTimeout(config.getIdleTimeout());
        hikariDataSource.setConnectionTimeout(config.getConnectionTimeout());
        return hikariDataSource;
    }

    @Bean(destroyMethod = "close")
    @ConfigurationProperties(prefix = "hikari")
    @Profile("hikari")
    @Primary
    public HikariDataSource hikariDataSource2() {
        return new HikariDataSource();
    }

    @Bean
    @Profile("hikari")
    public JdbcTemplate hikariJdbcTemplate(HikariDataSource hikariDataSource) {
        return new JdbcTemplate(hikariDataSource);
    }

    @Bean
    @Profile("hikari")
    public DataSourceTransactionManager hikariDataSourceTransactionManager(HikariDataSource hikariDataSource) {
        return new DataSourceTransactionManager(hikariDataSource);
    }

    @Bean
    @Profile("hikari")
    public TransactionTemplate hikariTransactionTemplate(DataSourceTransactionManager hikariDataSourceTransactionManager) {
        return new TransactionTemplate(hikariDataSourceTransactionManager);
    }

    /* ========== Druid ========== */
    @Bean(initMethod = "init", destroyMethod = "close")
    @Profile("druid")
    public DruidDataSource druidDataSource(SelfDruidConfig config) {
        DruidDataSource druidDataSource = new DruidDataSource();

        druidDataSource.setDriverClassName(config.getDriverClassName());
        druidDataSource.setUrl(config.getJdbcUrl());
        druidDataSource.setPassword(config.getPassword());
        druidDataSource.setUsername(config.getUsername());
        druidDataSource.setInitialSize(config.getInitialSize());
        druidDataSource.setMinIdle(config.getMinIdle());
        druidDataSource.setMaxActive(config.getMaxActive());
        druidDataSource.setMaxWait(config.getMaxWait());
        druidDataSource.setTimeBetweenEvictionRunsMillis(config.getTimeBetweenEvictionRunsMillis());
        druidDataSource.setTestOnBorrow(config.isTestOnBorrow());
        druidDataSource.setTestOnReturn(config.isTestOnReturn());
        druidDataSource.setMinEvictableIdleTimeMillis(config.getMinEvictableIdleTimeMillis());
        druidDataSource.setTestWhileIdle(config.isTestWhileIdle());

        return druidDataSource;
    }

    @Bean(initMethod = "init", destroyMethod = "close")
    @ConfigurationProperties(prefix = "druid")
    @Profile("druid")
    @Primary
    public DruidDataSource druidDataSource02() {
        return new DruidDataSource();
    }

    @Bean(name = "druidJdbcTemplate")
    @Profile("druid")
    public JdbcTemplate druidJdbcTemplate(DruidDataSource druidDataSource) {
        return new JdbcTemplate(druidDataSource);
    }

    @Bean(name = "druidDataSourceTransactionManager")
    @Profile("druid")
    public DataSourceTransactionManager druidDataSourceTransactionManager(DruidDataSource druidDataSource) {
        return new DataSourceTransactionManager(druidDataSource);
    }

    @Bean(name = "druidTransactionTemplate")
    @Profile("druid")
    public TransactionTemplate druidTransactionTemplate(DataSourceTransactionManager druidDataSourceTransactionManager) {
        return new TransactionTemplate(druidDataSourceTransactionManager);
    }

    /* ========== C3p0 ========== */
    @Bean(destroyMethod = "close")
    @Profile("c3p0")
    public ComboPooledDataSource  comboPooledDataSource(SelfC3p0Config config) throws PropertyVetoException {
        ComboPooledDataSource comboPooledDataSource = new ComboPooledDataSource();

        comboPooledDataSource.setDriverClass(config.getDriverClassName());
        comboPooledDataSource.setJdbcUrl(config.getJdbcUrl());
        comboPooledDataSource.setUser(config.getUsername());
        comboPooledDataSource.setPassword(config.getPassword());
        comboPooledDataSource.setInitialPoolSize(config.getInitialPoolSize());
        comboPooledDataSource.setMinPoolSize(config.getMinPoolSize());
        comboPooledDataSource.setMaxPoolSize(config.getMaxPoolSize());
        comboPooledDataSource.setAcquireIncrement(config.getAcquireIncrement());
        comboPooledDataSource.setCheckoutTimeout(config.getCheckoutTimeout());
        comboPooledDataSource.setIdleConnectionTestPeriod(config.getIdleConnectionTestPeriod());
        comboPooledDataSource.setMaxIdleTime(config.getMaxIdleTime());
        comboPooledDataSource.setTestConnectionOnCheckin(config.isTestConnectionOnCheckin());
        comboPooledDataSource.setTestConnectionOnCheckout(config.isTestConnectionOnCheckout());
        comboPooledDataSource.setNumHelperThreads(config.getNumHelperThreads());

        return comboPooledDataSource;
    }

    @Bean(destroyMethod = "close")
    @ConfigurationProperties(prefix = "combo")
    @Profile("c3p0")
    @Primary
    public ComboPooledDataSource  comboPooledDataSource02() {
        return new ComboPooledDataSource();
    }

    @Bean
    @Profile("c3p0")
    public JdbcTemplate comboJdbcTemplate(ComboPooledDataSource comboPooledDataSource) {
        return new JdbcTemplate(comboPooledDataSource);
    }

    @Bean
    @Profile("c3p0")
    public DataSourceTransactionManager comboDataSourceTransactionManager(ComboPooledDataSource comboPooledDataSource) {
        return new DataSourceTransactionManager(comboPooledDataSource);
    }

    @Bean
    @Profile("c3p0")
    public TransactionTemplate comboTransactionTemplate(DataSourceTransactionManager comboDataSourceTransactionManager) {
        return new TransactionTemplate(comboDataSourceTransactionManager);
    }

    /* ========== DBCP2 ========== */
    @Bean(destroyMethod = "close")
    @Profile("dbcp2")
    public BasicDataSource basicDataSource(SelfDbcp2Config config) {
        BasicDataSource basicDataSource = new BasicDataSource();

        basicDataSource.setDriverClassName(config.getDriverClassName());
        basicDataSource.setUrl(config.getJdbcUrl());
        basicDataSource.setPassword(config.getPassword());
        basicDataSource.setUsername(config.getUsername());
        basicDataSource.setInitialSize(config.getInitialSize());
        basicDataSource.setMaxTotal(config.getMaxTotal());
        basicDataSource.setMaxIdle(config.getMaxIdle());
        basicDataSource.setMinIdle(config.getMinIdle());
        basicDataSource.setMaxWaitMillis(config.getMaxWaitMillis());
        basicDataSource.setTimeBetweenEvictionRunsMillis(config.getTimeBetweenEvictionRunsMillis());
        basicDataSource.setMinEvictableIdleTimeMillis(config.getMinEvictableIdleTimeMillis());
        basicDataSource.setTestOnBorrow(config.isTestOnBorrow());
        basicDataSource.setTestOnReturn(config.isTestOnReturn());
        basicDataSource.setNumTestsPerEvictionRun(config.getNumTestsPerEvictionRun());
        basicDataSource.setTestWhileIdle(config.isTestWhileIdle());

        return basicDataSource;
    }

    @Bean(destroyMethod = "close")
    @ConfigurationProperties(prefix = "dbcp2")
    @Profile("dbcp2")
    @Primary
    public BasicDataSource basicDataSource02() {
        return new BasicDataSource();
    }

    @Bean
    @Profile("dbcp2")
    public JdbcTemplate basicJdbcTemplate(BasicDataSource basicDataSource) {
        return new JdbcTemplate(basicDataSource);
    }

    @Bean
    @Profile("dbcp2")
    public DataSourceTransactionManager basicDataSourceTransactionManager(BasicDataSource basicDataSource) {
        return new DataSourceTransactionManager(basicDataSource);
    }

    @Bean
    @Profile("dbcp2")
    public TransactionTemplate basicTransactionTemplate(DataSourceTransactionManager basicDataSourceTransactionManager) {
        return new TransactionTemplate(basicDataSourceTransactionManager);
    }

}
