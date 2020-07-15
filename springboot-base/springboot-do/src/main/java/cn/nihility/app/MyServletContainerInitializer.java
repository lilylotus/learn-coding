package cn.nihility.app;

import cn.nihility.app.job.IDoMyJob;
import cn.nihility.app.servlet.MyHttpServlet;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;
import javax.servlet.annotation.HandlesTypes;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.UUID;

/**
 *
 * 自定义的 web Servlet 加载容器，类似 web.xml 当中的 Servlet 配置
 *
 * @author dandelion
 * @date 2020-03-24 22:11
 */
@HandlesTypes(IDoMyJob.class)
public class MyServletContainerInitializer implements ServletContainerInitializer {

    @Override
    public void onStartup(Set<Class<?>> c, ServletContext ctx) {
        System.out.println("======== MyServletContainerInitializer onStartUp ========");

        c.forEach(System.out::println);
        System.out.println("---------------------");
        c.forEach(job -> {
            try {
                Object instance = job.newInstance();
                Method method = job.getDeclaredMethod("doJob", String.class);
                method.setAccessible(true);
                method.invoke(instance, UUID.randomUUID().toString());

            } catch (InstantiationException | IllegalAccessException
                    | NoSuchMethodException | InvocationTargetException e) {
                e.printStackTrace();
            }
        });

        System.out.println("-------- register configuration all servlet.");
        ServletRegistration.Dynamic registration = ctx.addServlet("myHttpServlet", new MyHttpServlet());
        registration.addMapping("/servlet/*");

    }

}
