package cn.nihility.boot;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

// 使用 AspectJ AOP 代替 Spring AOP，@Transactional 就可以在类调用自己方法生效
// 注意：要添加 aspect 的依赖，和 compiler 插件
// (mode = AdviceMode.ASPECTJ)
@EnableTransactionManagement
@EnableCaching
@SpringBootApplication
@MapperScan(basePackages = {"cn.nihility.boot.mybatis.dao"})
public class SpringbootProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootProjectApplication.class, args);
    }

}
