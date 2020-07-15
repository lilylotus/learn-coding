package cn.nihility.app.servlet;

import org.apache.commons.io.FileUtils;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * 自己创建的 Servlet
 *
 * @author dandelion
 * @date 2020-03-24 22:14
 */
public class MyHttpServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("======== MyHttpServlet doGet");

        String contextPath = request.getContextPath();
        String requestURI = request.getRequestURI();
        System.out.println("requestURI [" + requestURI + "] contextPath [" + contextPath + "]");
        String sourcePath = requestURI.replace(contextPath, "");
        System.out.println("sourcePath [" + sourcePath + "]");

        String resourcesPath = MyHttpServlet.class.getResource("/resources").getPath();
        System.out.println("Resources path [" + resourcesPath + "]");

        String filePath = resourcesPath + sourcePath;
        System.out.println("file Path [" + filePath + "]");
        File sourceFile = new File(filePath);

        response.setContentType("text/html;charset=UTF-8");

        if (sourceFile.exists()) {
            ServletOutputStream outputStream = response.getOutputStream();

            try (FileInputStream fi = new FileInputStream(sourceFile)) {
                byte[] buffer = new byte[1024];
                int len;
                while ((len = fi.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, len);
                }
            }

            outputStream.flush();
        } else {
            response.getWriter().println(filePath + " not exist. ======== MyHttpServlet doGet");
        }

    }
}
