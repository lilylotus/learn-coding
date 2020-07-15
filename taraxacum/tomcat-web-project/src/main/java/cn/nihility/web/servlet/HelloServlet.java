package cn.nihility.web.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author dandelion
 * @date 2020:06:27 13:31
 */
@WebServlet(name = "helloServlet", urlPatterns = {"/hello/servlet"})
public class HelloServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("HelloServlet -> doGet");

        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=utf-8");

        response.getWriter().println("{\"servlet\":\"HelloServlet\"}");
        response.getWriter().flush();

    }
}
