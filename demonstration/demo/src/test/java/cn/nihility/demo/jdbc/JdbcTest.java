package cn.nihility.demo.jdbc;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
class JdbcTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void testJdbc() {
        Assertions.assertNotNull(jdbcTemplate);
    }

    @Test
    void testJdbcTemplate() {
        Assertions.assertNotNull(jdbcTemplate);

        List<Map<String, Object>> result = jdbcTemplate.query("SELECT id, name_english, name_chinese, age, add_time, update_time FROM flower",
            (ResultSet rs, int rowNum) -> {
                Map<String, Object> rt = new HashMap<>();
                rt.put("index", rowNum);
                rt.put("id", rs.getString("id"));
                rt.put("name_english", rs.getString("name_english"));
                rt.put("name_chinese", rs.getString("name_chinese"));
                rt.put("age", rs.getInt("age"));
                rt.put("add_time", rs.getString("add_time"));
                rt.put("update_time", rs.getString("update_time"));
                return rt;
            }
        );

        Assertions.assertEquals(1, result.size());

        System.out.println(result.get(0));
    }

}
