package cn.nihility.api.config;

import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ServletComponentScan(basePackages = {"cn.nihility.api.filter"})
public class ServletComponentConfiguration {

}
