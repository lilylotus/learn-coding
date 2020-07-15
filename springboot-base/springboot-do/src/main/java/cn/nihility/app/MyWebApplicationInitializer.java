package cn.nihility.app;

import cn.nihility.app.config.WebApplicationConfig;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

/**
 * MyWebApplicationInitializer
 * 自定义的 web 应用初始化器，仿 spring boot 自启动
 * 类似于在 spring framework mvc 中 web.xml 中配置的 Listener 和 DispatcherServlet
 * 和配置自动扫描包
 *
 * @author dandelion
 * @date 2020-03-24 21:57
 */
public class MyWebApplicationInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        // 初始 Listener ， ApplicationContextListener
        System.out.println("======== MyWebApplicationInitializer onStartup ========");
        AnnotationConfigWebApplicationContext context =
                new AnnotationConfigWebApplicationContext();
        context.register(WebApplicationConfig.class); // 类似于 spring boot 的启动类配置
        context.refresh();

        // DispatcherServlet 配置, 创建一个调度器
        DispatcherServlet dispatcherServlet = new DispatcherServlet(context);
        // 注册 Servlet 到 web 上下文当中
        ServletRegistration.Dynamic registration = servletContext.addServlet("dispatcherServlet", dispatcherServlet);
        // 配置启动优先级和拦截路径
        registration.addMapping("/mvc/*");
        registration.setLoadOnStartup(1);

    }

}
