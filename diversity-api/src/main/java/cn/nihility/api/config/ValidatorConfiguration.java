package cn.nihility.api.config;

import org.hibernate.validator.HibernateValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

/**
 * 校验模式：
 * 默认会校验完所有属性，然后将错误信息一起返回
 * 设置为只要有一个校验失败了，其它就不必校验了
 *
 * @author nihility
 * @date 2022/03/04 16:17
 */
@Configuration
public class ValidatorConfiguration {

    @Bean
    public Validator validator() {
        ValidatorFactory validatorFactory = Validation.byProvider(HibernateValidator.class)
            .configure()
            .failFast(true)
            .buildValidatorFactory();
        return validatorFactory.getValidator();
    }

}
