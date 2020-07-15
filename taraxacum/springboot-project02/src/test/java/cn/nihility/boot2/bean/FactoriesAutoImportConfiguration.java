package cn.nihility.boot2.bean;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author dandelion
 * @date 2020:06:27 15:25
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages = {"cn.nihility.boot2"})
public class FactoriesAutoImportConfiguration {
}
