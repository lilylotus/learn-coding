package cn.nihility.boot.mybatis.dao;

import cn.nihility.boot.mybatis.dto.TestDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Springboot + mybatis 测试 dao
 *
 * @author clover
 * @date 2020-01-09 00:23
 */
@Mapper
public interface TestDao {
    List<TestDto> selectAll();
}
