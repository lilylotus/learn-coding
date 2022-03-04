package cn.nihility.api.dto;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

/**
 * @author nihility
 * @date 2022/03/04 13:46
 */
public class ValidateDto {

    @NotEmpty(message = "校验名称不可为空")
    @Length(max = 6, message = "名称长度不可超过 6 个字符")
    private String name;

    @Min(value = 0, message = "校验年龄不可小于 0")
    @Max(value = 100, message = "校验年龄不可大于 100")
    private Integer age;

    @Email
    @NotEmpty(message = "邮件不可为空")
    private String email;

    @Override
    public String toString() {
        return "ValidateDto{" +
            "name='" + name + '\'' +
            ", age=" + age +
            ", email=" + email +
            '}';
    }

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
