package cn.nihility.web;

import cn.nihility.web.configuration.SpringWebApplicationConfiguration;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

/**
 * Servlet 3.0+ 的环境中 spring SpringServletContainerInitializer 会被自动启动加载
 * SpringServletContainerInitializer 详细加载机制，加载 web 配置到 spring 容器中
 * 不用在写 web.xml 配置
 *
 * @author dandelion
 * @date 2020-06-25 17:15
 */
public class MyWebApplicationInitializer implements WebApplicationInitializer {
    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        System.out.println("========== MyWebApplicationInitializer onStartup ==========");

        // 加载启动 spring web 应用的配置, 初始化 spring web context 环境
        AnnotationConfigWebApplicationContext webApplicationContext = new AnnotationConfigWebApplicationContext();
        webApplicationContext.register(SpringWebApplicationConfiguration.class);
        webApplicationContext.refresh();

        // 注册 spring context listener 到 servletContext, 注意这里要把 spring web 的上下文配置进去
//        DispatcherServlet dispatcherServlet = new DispatcherServlet(webApplicationContext);
        DispatcherServlet dispatcherServlet = new DispatcherServlet();
        dispatcherServlet.setApplicationContext(webApplicationContext);

        // 加载 调度 Servlet 到 web context
        // http://localhost:8080/mvc/web/initial -> 映射到对应的 controller
        ServletRegistration.Dynamic servlet = servletContext.addServlet("dispatcherServlet", dispatcherServlet);
        servlet.addMapping("/mvc/*");
        servlet.setLoadOnStartup(1);
    }
}
