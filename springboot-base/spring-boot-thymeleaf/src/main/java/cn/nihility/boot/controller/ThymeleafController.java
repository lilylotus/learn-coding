package cn.nihility.boot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * ThymeleafController
 *
 * @author dandelion
 * @date 2020-04-11 23:44
 */
@Controller
@RequestMapping("/thymeleaf")
public class ThymeleafController {

    @RequestMapping(value = {"/hello"}, method = RequestMethod.GET)
    public String hello(Model model) {
        model.addAttribute("msg", "Thymeleaf Spring Boot 学习 ");
        return "hello";
    }

}
