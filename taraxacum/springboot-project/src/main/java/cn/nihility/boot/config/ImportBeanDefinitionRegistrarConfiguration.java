package cn.nihility.boot.config;

import cn.nihility.boot.annotation.EnableRegistrar;
import cn.nihility.boot.annotation.RegistrarScan;

/**
 * @author dandelion
 * @date 2020:06:27 19:10
 */
//@Configuration -> spring.factories
@EnableRegistrar
@RegistrarScan(basePackage = "cn.nihility.boot.registrar.mapper")
public class ImportBeanDefinitionRegistrarConfiguration {
}
