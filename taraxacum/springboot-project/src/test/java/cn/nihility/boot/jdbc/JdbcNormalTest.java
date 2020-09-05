package cn.nihility.boot.jdbc;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.List;

/**
 * @author dandelion
 * @date 2020:06:26 20:26
 */
@SpringBootTest
public class JdbcNormalTest {

    @Autowired
    private DataSource dataSource;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private JdbcCaching jdbcDemonstrate;

    @Test
    public void testDataSourceType() {
        System.out.println(dataSource.getClass());
        System.out.println(jdbcTemplate);
    }

    @Test
    public void testJdbcQuery() {
        Assertions.assertNotNull(jdbcTemplate);

        jdbcTemplate.query("SELECT id, name, money from bank", rs -> {
            System.out.println(rs.getInt("id") + " : " + rs.getString("name") + " : " + rs.getInt("money"));
        });
    }

    @Test
    public void testJdbcCaching() {
        List<JdbcCaching.SpringBean> springBeans = jdbcDemonstrate.querySpringBeanList();
        Assertions.assertNotNull(springBeans);
        int hashCode = springBeans.hashCode();
        System.out.println(hashCode);

        List<JdbcCaching.SpringBean> springBeans1 = jdbcDemonstrate.querySpringBeanList();
        Assertions.assertNotNull(springBeans1);
        int hashCode1 = springBeans1.hashCode();
        System.out.println(hashCode1);
        Assertions.assertEquals(hashCode, hashCode1);
    }

}
