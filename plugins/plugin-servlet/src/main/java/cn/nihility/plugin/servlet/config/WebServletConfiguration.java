package cn.nihility.plugin.servlet.config;

import cn.nihility.plugin.servlet.registrar.ServletFilterComponentScan;
import org.hibernate.validator.HibernateValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

@Configuration
public class WebServletConfiguration {

    @Configuration
    @ServletFilterComponentScan(basePackages = {"cn.nihility.plugin.servlet.filter"})
    public static class ServletFilterComponentScanConfiguration {
    }

    @Bean
    public GlobalExceptionHandler globalExceptionHandler() {
        return new GlobalExceptionHandler();
    }

    @Bean
    public Validator validator() {
        ValidatorFactory validatorFactory = Validation.byProvider(HibernateValidator.class)
            .configure()
            .failFast(true)
            .buildValidatorFactory();
        return validatorFactory.getValidator();
    }

}
