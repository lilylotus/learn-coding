package cn.nihility.plugin.mybatis.test;

import cn.nihility.mybatis.service.MybatisBatchService;
import cn.nihility.plugin.mybatis.mapper.FlowerMapper;
import cn.nihility.plugin.mybatis.mapper.UserMapper;
import cn.nihility.plugin.mybatis.pojo.Flower;
import cn.nihility.plugin.mybatis.pojo.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SpringBootTest
class MybatisBatchTest {

    @Autowired
    private MybatisBatchService mybatisBatchService;

    @Test
    void mybatisBatchTest() {
        Assertions.assertNotNull(mybatisBatchService);

        List<Flower> flowers = new ArrayList<>();
        int size = 10;

        for (int i = 0; i < size; i++) {
            String id = UUID.randomUUID().toString().replace("-", "");
            flowers.add(new Flower(id, id + "_english_name", id + "_chinese_name", 10));
        }

        int count = mybatisBatchService.batchUpdateOrInsert(flowers, FlowerMapper.class, ((flower, flowerMapper) -> {
            flowerMapper.insertByEntity(flower);
        }));

        Assertions.assertEquals(size, count);
    }


    @Test
    void mybatisBatchPlusMapperTest() {
        Assertions.assertNotNull(mybatisBatchService);
        int size = 10;

        List<User> users = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            String name = UUID.randomUUID().toString().replace("-", "");
            users.add(new User(name, 20, name.substring(0, 8) + "@example.com"));
        }

        int ct = mybatisBatchService.batchUpdateOrInsert(users, UserMapper.class, ((user, userMapper) -> userMapper.insert(user)));

        Assertions.assertEquals(size, ct);
    }

}
