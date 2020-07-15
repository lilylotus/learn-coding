package cn.nihility.web;

import cn.nihility.web.initializer.IAppInitializer;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.HandlesTypes;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author dandelion
 * @date 2020:06:27 13:16
 */
@HandlesTypes(IAppInitializer.class)
public class Servlet31ContainerInitializer implements ServletContainerInitializer {
    @Override
    public void onStartup(Set<Class<?>> clazzSet, ServletContext servletContext) throws ServletException {
        System.out.println("Servlet3.1 规定 META-INF/javax.servlet.ServletContainerInitializer 运行起来了");

        final List<IAppInitializer> initializers = new LinkedList<>();

        if (clazzSet != null) {
            clazzSet.forEach(clazz -> {
               if (!clazz.isInterface()
                       && IAppInitializer.class.isAssignableFrom(clazz)
                       && !Modifier.isAbstract(clazz.getModifiers())) {
                   try {
                       initializers.add((IAppInitializer) clazz.newInstance());
                   } catch (InstantiationException | IllegalAccessException e) {
                       e.printStackTrace();
                       System.out.println("实例化 IAppInitializer 失败 : " + e.getMessage());
                   }
               }
            });
        }

        servletContext.log(initializers.size() + " 个 IAppInitializer 实现类在 classpath 出没");
        System.out.println(initializers.size() + " 个 IAppInitializer 实现类在 classpath 出没");

        if (initializers.isEmpty()) {
            servletContext.log("木有找到 cn.nihility.servlet.IAppInitializer");
            System.out.println("木有找到 cn.nihility.servlet.IAppInitializer");
        } else {
            initializers.forEach(clazz -> {
                try {
                    clazz.onAppStartup(servletContext);
                } catch (ServletException e) {
                    System.out.println("带起 " + clazz.getClass().getName() + " 失败");
                    e.printStackTrace();
                }
            });
        }

    }
}
