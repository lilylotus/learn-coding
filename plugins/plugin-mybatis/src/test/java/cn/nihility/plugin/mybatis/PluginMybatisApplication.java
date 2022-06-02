package cn.nihility.plugin.mybatis;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"cn.nihility.plugin.mybatis", "cn.nihility.mybatis"})
@MapperScan(basePackages = {"cn.nihility.plugin.mybatis.mapper"})
public class PluginMybatisApplication {

    public static void main(String[] args) {
        SpringApplication.run(PluginMybatisApplication.class, args);
    }

}
