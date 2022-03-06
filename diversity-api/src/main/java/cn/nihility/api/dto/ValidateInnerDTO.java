package cn.nihility.api.dto;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

/**
 * @author sakura
 * @date 2022-03-06 14:03
 */
public class ValidateInnerDTO {

    @NotEmpty(message = "内部名称不能为空")
    @Length(max = 10, message = "名称不可超过 10 个字符")
    private String name;

    @Min(value = 0, message = "内部校验年龄不可小于 0")
    @Max(value = 100, message = "内部校验年龄不可大于 100")
    private Integer age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "ValidateInnerDTO{" +
            "name='" + name + '\'' +
            ", age=" + age +
            '}';
    }
}
