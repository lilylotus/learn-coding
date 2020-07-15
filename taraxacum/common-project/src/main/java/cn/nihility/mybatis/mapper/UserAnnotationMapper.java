package cn.nihility.mybatis.mapper;

import cn.nihility.mybatis.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * UserAnnotationMapper
 *
 * @author dandelion
 * @date 2020-04-01 15:28
 */
@Mapper
public interface UserAnnotationMapper {

    @Select("SELECT id, name, age, address from mybatis_user")
    List<User> getAllUser();

    @Select("SELECT id, name, age, address from mybatis_user where id = #{param1}")
    User getUserById(Integer id);

}
