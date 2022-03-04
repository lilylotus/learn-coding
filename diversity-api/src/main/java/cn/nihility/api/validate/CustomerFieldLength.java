package cn.nihility.api.validate;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * @author nihility
 * @date 2022/03/04 15:42
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Constraint(validatedBy = {CustomerFieldLengthValidate.class})
public @interface CustomerFieldLength {

    String message() default "自定义校验参数失败";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * @return value the element must be lower or equal to
     */
    int value() default 0;

}
