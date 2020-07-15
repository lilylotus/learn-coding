package cn.nihility.learn.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

/**
 * AutoImportBeanConfig
 * 自动配置类的二次配置类
 * @author dandelion
 * @date 2020-03-24 17:39
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AutoImportBeanConfig {

    private String name;
    private String address;
    private Integer age;

    public AutoImportBeanConfig(AutoImportBean autoImportBean) {
        this.name = Optional.ofNullable(autoImportBean).map(obj -> obj.getName() + " reconfiguration").orElse("object is null");
        this.age = Optional.ofNullable(autoImportBean).map(obj -> obj.getAge() + 10).orElse(0);
        this.address = Optional.ofNullable(autoImportBean).map(obj -> obj.getName() + " reconfiguration").orElse("object is null");
    }

}
