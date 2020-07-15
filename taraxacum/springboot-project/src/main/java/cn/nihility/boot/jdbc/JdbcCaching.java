package cn.nihility.boot.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * @author dandelion
 * @date 2020:06:26 20:36
 */
@Service
//@EnableCaching
public class JdbcCaching implements Serializable {
    private static final long serialVersionUID = 7426984968194232865L;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Cacheable("ehcache")
    public List<SpringBean> querySpringBeanList() {
        Objects.requireNonNull(jdbcTemplate);
        return jdbcTemplate.query("select id, name, money from bank", ((rs, rowNum) -> {
            SpringBean bean = new SpringBean();
            bean.setId(rs.getInt("id"));
            bean.setName(rs.getString("name"));
            bean.setMoney(rs.getInt("money"));
            return bean;
        }));
    }

    public static class SpringBean implements Serializable {
        private static final long serialVersionUID = 6836192790933173798L;

        private Integer id;
        private String name;
        private Integer money;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getMoney() {
            return money;
        }

        public void setMoney(Integer money) {
            this.money = money;
        }

        @Override
        public String toString() {
            return "SpringBean{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", money=" + money +
                    '}';
        }
    }

}
