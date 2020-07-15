package cn.nihility.web.initializer;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/**
 * @author dandelion
 * @date 2020:06:27 13:17
 */
public interface IAppInitializer {
    void onAppStartup(ServletContext servletContext) throws ServletException;
}
