package cn.nihility.plugin.mybatis.mapper;

import cn.nihility.plugin.mybatis.pojo.Flower;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface FlowerMapper {

    List<Flower> searchAll();

    Flower searchById(String id);

    Integer insertByEntity(Flower flower);

    Integer batchInsertListEntity(@Param("flowerList") List<Flower> flowerList);

}
