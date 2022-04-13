package cn.nihility.plugin.mybatis.test;

import cn.nihility.plugin.mybatis.mapper.UserMapper;
import cn.nihility.plugin.mybatis.pojo.User;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class MybatisPlusMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    void testSelect() {
        System.out.println(("----- selectAll method test ------"));
        List<User> userList = userMapper.selectList(null);
        Assertions.assertNotNull(userList);
        userList.forEach(System.out::println);
    }

    @Test
    void testInsert() {
        User user = new User();
        user.setEmail("insert_email@test.com");
        user.setAge(20);
        user.setName("insertName");
        int insertResult = userMapper.insert(user);
        Assertions.assertEquals(1, insertResult);
    }

    @Test
    void testUpdate() {
        User user = new User();
        user.setEmail("update_email@test.com");
        user.setName("updateName");
        user.setId(1L);
        int count = userMapper.updateById(user);
        Assertions.assertEquals(1, count);
    }

    @Test
    void testQueryWrapper() {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        User user = new User();
        user.setId(4L);
        wrapper.setEntity(user);
        User selectResultUser = userMapper.selectOne(wrapper);
        Assertions.assertNotNull(selectResultUser);
        System.out.println(selectResultUser);
    }

}
