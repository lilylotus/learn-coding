package cn.nihility.boot;

import cn.nihility.boot.annotation.EnableUserBean;
import cn.nihility.boot.bean.User;
import cn.nihility.boot.config.UserConfiguration;
import cn.nihility.boot.suports.AutoImportImpl01;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * TestEnableApplication
 *
 * @author dandelion
 * @date 2020-04-25 13:02
 */
@EnableUserBean
@EnableAutoConfiguration
public class TestEnableApplication {

    public static void main(String[] args) {
        /**
         * --> EnableUserBean --> 自动 import UserImportSelector
         * --> UserConfiguration --> user
         */
        /* 获取 spring 容器 */
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(TestEnableApplication.class);
        final User user = context.getBean(User.class);
        System.out.println(user);

        final UserConfiguration userConfiguration = context.getBean(UserConfiguration.class);
        System.out.println(userConfiguration);

        final AutoImportImpl01 impl01 = context.getBean(AutoImportImpl01.class);
        System.out.println(impl01);
    }

}
