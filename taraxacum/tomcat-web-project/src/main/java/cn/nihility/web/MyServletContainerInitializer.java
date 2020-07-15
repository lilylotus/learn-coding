package cn.nihility.web;

import cn.nihility.web.initializer.ServletApplicationInitializer;
import cn.nihility.web.servlet.MvcHttpServlet;
import cn.nihility.web.servlet.MyHtmlServlet;
import cn.nihility.web.servlet.MyServlet;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.annotation.HandlesTypes;
import java.util.Set;

/**
 *  1. Servlet 3.0 容器会在初始化的时候自动调用实现了接口
 *  javax.servlet.ServletContainerInitializer 的类
 *  2. 会把 @HandlesTypes 注解中的 接口实现类/类 加载放到 Set<Class<?>> 入参中
 * @author dandelion
 * @date 2020-06-25 16:48
 */
@HandlesTypes(ServletApplicationInitializer.class)
public class MyServletContainerInitializer implements ServletContainerInitializer {
    @Override
    public void onStartup(Set<Class<?>> c, ServletContext ctx) throws ServletException {
        System.out.println("========== MyServletContainerInitializer onStartup ==========");

        if (c != null) {
            c.forEach(clazz -> System.out.println(clazz.getName()));

            /* 实现加载类的初始化 */
            c.forEach(clazz -> {
                try {
                    ServletApplicationInitializer instance = (ServletApplicationInitializer) clazz.newInstance();
                    instance.init("MyServletContainerInitializer invoke init method");
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            });
        }

        System.out.println("========== Registry Servlet");
        ServletRegistration.Dynamic servlet = ctx.addServlet("myServlet", new MyServlet());
        // /servlet/*
        servlet.addMapping("/servlet/myServlet");

        ServletRegistration.Dynamic htmlServlet = ctx.addServlet("myHtmlServlet", new MyHtmlServlet());
        htmlServlet.addMapping("/servlet/*");

        ServletRegistration.Dynamic mvcServlet = ctx.addServlet("mvcServlet", new MvcHttpServlet());
        mvcServlet.addMapping("/mvc-servlet/*");
    }
}
