package cn.nihility.web.servlet;

import com.alibaba.fastjson.JSON;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author dandelion
 * @date 2020:06:27 13:34
 */
@WebServlet(name = "loginServlet", urlPatterns = {"/servlet31/login"})
public class LoginServlet extends HttpServlet {

    private static final String PASSWORD = "luck";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("------> LoginServlet -> doPost");

        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=utf-8");

        String userName = request.getParameter("userName");
        String password = request.getParameter("password");

        Map<String, Object> respMap = new HashMap<>();
        if (PASSWORD.equals(password)) {
            respMap.put("success", true);
            respMap.put("message", "登录成功");
            request.getSession().setAttribute("userName", userName);
        } else {
            respMap.put("success", false);
            respMap.put("message", "登录失败，密码错误。");
        }

        response.getWriter().write(JSON.toJSONString(respMap));
    }
}
