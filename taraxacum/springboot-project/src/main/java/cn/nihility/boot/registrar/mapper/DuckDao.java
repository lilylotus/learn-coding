package cn.nihility.boot.registrar.mapper;

import cn.nihility.boot.annotation.RegistrarMapper;
import cn.nihility.boot.annotation.RegistrarParam;
import cn.nihility.boot.annotation.RegistrarSelect;
import cn.nihility.boot.registrar.dto.Duck;

/**
 * @author dandelion
 * @date 2020:06:27 18:22
 */
@RegistrarMapper
public interface DuckDao {

    @RegistrarSelect("SELECT id, name, age FROM duck id = #{id}")
    Duck findDuckById(@RegistrarParam("id") Integer paramId);

}
