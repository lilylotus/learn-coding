package cn.nihility.web.initializer;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/**
 * @author dandelion
 * @date 2020:06:27 13:17
 */
public class AppInitializerImpl implements IAppInitializer {
    @Override
    public void onAppStartup(ServletContext servletContext) throws ServletException {
        System.out.println("AppInitializerImpl -> onAppStartup");
        System.out.println("AppInitializerImpl 是被 /resources/META-INF/services/javax.servlet.ServletContainerInitializer 启动带起来的");
    }
}
