package cn.nihility.api.dto;

import cn.nihility.api.validate.CustomerFieldLength;
import cn.nihility.api.validate.ValidateAddGroup;
import cn.nihility.api.validate.ValidateEditGroup;
import org.hibernate.validator.constraints.Length;

import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * @author nihility
 * @date 2022/03/04 13:46
 */
public class ValidateDto {

    @NotEmpty(message = "姓名不可为空", groups = {ValidateAddGroup.class, ValidateEditGroup.class})
    @Length(max = 6, message = "姓名不可超过 6 个字符")
    private String name;

    @Min(value = 0, message = "年龄不可小于 0")
    @Max(value = 100, message = "年龄不可大于 100")
    private Integer age;

    @Email(message = "邮件格式不正确")
    @NotEmpty(message = "邮件不可为空")
    private String email;

    @CustomerFieldLength(groups = {ValidateAddGroup.class},
        message = "自定义字段不可为空且长度不超过 10 个字符", value = 10)
    private String customer;

    @Valid
    @NotNull(message = "内置校验字段不可为空")
    private ValidateInnerDTO validateInner;

    @Override
    public String toString() {
        return "ValidateDto{" +
            "name='" + name + '\'' +
            ", age=" + age + '\'' +
            ", email=" + email + '\'' +
            ", customer=" + customer + '\'' +
            ", validateInner=" + validateInner + '\'' +
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

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public ValidateInnerDTO getValidateInner() {
        return validateInner;
    }

    public void setValidateInner(ValidateInnerDTO validateInner) {
        this.validateInner = validateInner;
    }

}
