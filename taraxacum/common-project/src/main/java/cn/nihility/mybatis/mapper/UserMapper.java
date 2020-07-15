package cn.nihility.mybatis.mapper;

import cn.nihility.mybatis.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * UserMapper
 *
 * @author dandelion
 * @date 2020-04-01 13:05
 */
@Mapper
public interface UserMapper {

    List<User> getAllUser();

    User getUserById(Integer id);

    @Select("SELECT id, name, age, address FROM mybatis_user WHERE age = #{param1}")
    List<User> selectByAge(Integer age);

}
