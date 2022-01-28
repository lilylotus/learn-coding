package cn.nihility.spring.security.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author orchid
 * @date 2021-05-11 00:07:38
 */
@Controller
public class LoginController {

    @RequestMapping("/toLogin")
    public String toLogin() {
        return "login";
    }

    @RequestMapping("/toLogout")
    public String toLogout() {
        return "logout";
    }

    @PostMapping("/toSuccess")
    public String toSuccess() {
        return "redirect:/success.html";
    }

    @PostMapping("/toFailure")
    public String toFailure() {
        return "redirect:/failure.html";
    }

}
