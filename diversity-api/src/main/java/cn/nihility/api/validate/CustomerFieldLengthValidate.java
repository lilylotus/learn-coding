package cn.nihility.api.validate;

import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author nihility
 * @date 2022/03/04 15:27
 */
public class CustomerFieldLengthValidate implements ConstraintValidator<CustomerFieldLength, String> {

    int length;

    @Override
    public void initialize(CustomerFieldLength constraintAnnotation) {
        length = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return StringUtils.isNotBlank(value) && value.length() <= length;
    }

}
