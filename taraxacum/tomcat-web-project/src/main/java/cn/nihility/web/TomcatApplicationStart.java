package cn.nihility.web;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;

/**
 * tomcat 插件依赖方式启动
 *
 * @author dandelion
 * @date 2020-06-25 21:24
 */
public class TomcatApplicationStart {

    public static void main(String[] args) {
        Tomcat tomcat = new Tomcat();

        tomcat.setPort(8080);

        /* /out/production/classes/ */
        String rootPath = TomcatApplicationStart.class.getResource("/").getPath();
        System.out.println(rootPath);

        String replacePath = "/out/production/classes/";
        // build/libs/exploded/tomcat-web-project-1.0-SNAPSHOT.war/WEB-INF
        String webPath = "/build/libs/tomcat-web-project-1.0-SNAPSHOT.war";
        String webLocation = rootPath.replace(replacePath, "") + webPath;
        System.out.println(webLocation);

        Context webappContext = tomcat.addWebapp("", webLocation);
        WebResourceRoot standardRoot = new StandardRoot(webappContext);
        standardRoot.addPreResources(new DirResourceSet(standardRoot, "/WEB-INFO/class", rootPath, "/"));

        webappContext.setResources(standardRoot);

        try {
            tomcat.start();
            tomcat.getServer().await();
        } catch (LifecycleException e) {
            e.printStackTrace();
        }


    }

}
