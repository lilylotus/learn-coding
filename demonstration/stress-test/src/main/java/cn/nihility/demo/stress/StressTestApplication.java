package cn.nihility.demo.stress;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = {"cn.nihility.demo.stress.mapper"})
public class StressTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(StressTestApplication.class, args);
    }

}
