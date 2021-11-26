package cn.nihility.plugin.mybatis;

import cn.nihility.plugin.mybatis.mapper.FlowerMapper;
import cn.nihility.plugin.mybatis.pojo.Flower;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootTest
class MybatisTest {

    @Autowired
    private FlowerMapper flowerMapper;

    @Test
    void testSearchAll() {
        List<Flower> flowers = flowerMapper.searchAll();
        Assertions.assertNotNull(flowers);
        System.out.println(Stream.of(flowers).map(Object::toString).collect(Collectors.joining()));
    }

    @Test
    void testInsert() {
        Assertions.assertNotNull(flowerMapper);
        Flower flower = new Flower();
        flower.setId(UUID.randomUUID().toString().replace("-", "").substring(0, 10));
        flower.setAge(10);
        flower.setNameChinese("中文");
        flower.setNameEnglish("English");
        Integer integer = flowerMapper.insertByEntity(flower);
        Assertions.assertEquals(1, integer);
    }

}
