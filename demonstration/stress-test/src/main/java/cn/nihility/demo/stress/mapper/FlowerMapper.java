package cn.nihility.demo.stress.mapper;

import cn.nihility.demo.stress.pojo.Flower;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FlowerMapper {

    Flower searchById(String id);

}
