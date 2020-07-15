package cn.nihility.web.spring.controller;

import cn.nihility.web.annotation.MyController;
import cn.nihility.web.annotation.MyParam;
import cn.nihility.web.annotation.MyRequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * MyMvcController
 *
 * @author dandelion
 * @date 2020-06-25 20:18
 */
@MyController
@MyRequestMapping("/mvc-servlet/my")
public class MyMvcController {

    @MyRequestMapping("/hei")
    public String hei(HttpServletRequest request, @MyParam("user") String paramUser,
                      HttpServletResponse response, @MyParam("password") String paramPassword) {
        System.out.println("========== MyMvcController hei()");
        System.out.println("paramUser -> " + paramUser + " , paramPassword " + paramPassword
                + " , request session id : " + request.getSession().getId());

        StringBuilder sb = new StringBuilder();
        sb.append("user : ").append(paramUser);
        sb.append("<br>");
        sb.append("password : ").append(paramPassword);

        response.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");
        try {
            response.getWriter().write(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "servlet";
    }

}
