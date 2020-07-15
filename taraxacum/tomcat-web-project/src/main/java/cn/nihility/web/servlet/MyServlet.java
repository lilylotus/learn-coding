package cn.nihility.web.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * MyServlet
 *
 * @author dandelion
 * @date 2020-06-25 16:53
 */
public class MyServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("========== MyServlet doPost");

        String contextPath = request.getContextPath();
        String requestURI = request.getRequestURI();
        System.out.println("contextPath -> " +  contextPath + " , requestURI -> " + requestURI);

        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().print("{\"name\":\"servlet name\", \"age\":20}");
        response.getWriter().flush();

    }
}
