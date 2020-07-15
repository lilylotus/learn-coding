package cn.nihility.web.servlet;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * MyHtmlServlet
 *
 * @author dandelion
 * @date 2020-06-25 20:31
 */
public class MyHtmlServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        System.out.println("========== MyHtmlServlet doPost");

        final String sourceDir = MyHtmlServlet.class.getResource("/resources").getPath();
        System.out.println("Sources dir : " + sourceDir);

        String requestURI = request.getRequestURI();
        String contextPath = request.getContextPath();
        String finalUri = requestURI.replace(contextPath, "");
        System.out.println("requestURI : " + requestURI + " , contextPath : " + contextPath + " , finalUri : " + finalUri);

        final String loadFilePath = sourceDir + finalUri;
        System.out.println("load file path " + loadFilePath);

        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        FileInputStream fis = null;
        ServletOutputStream outputStream = null;

        try {
            request.setCharacterEncoding("UTF-8");

            File file = new File(loadFilePath);
            if (file.exists()) {
                fis = new FileInputStream(file);
                outputStream = response.getOutputStream();
                byte[] buffer = new byte[1024];
                int len;

                while ((len = fis.read(buffer)) != -1) {
                    System.out.println("read file len - " + len);
                    outputStream.write(buffer, 0, len);
                }
                outputStream.flush();

            } else {
                System.out.println(loadFilePath + " can not find");
                response.getWriter().print(loadFilePath + " can not find.");
                response.flushBuffer();
            }

            if (outputStream != null) {
                outputStream.print("<br \\> path : " + loadFilePath);
                outputStream.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != outputStream) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
