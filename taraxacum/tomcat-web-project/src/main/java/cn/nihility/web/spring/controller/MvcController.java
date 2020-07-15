package cn.nihility.web.spring.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * MvcController
 *
 * @author dandelion
 * @date 2020-06-25 17:25
 */
@Controller
public class MvcController {

    public MvcController() {
        System.out.println("========== MvcController constructor");
    }

    @RequestMapping(path = "/web/initial", method = RequestMethod.GET)
    public String initial() {
        System.out.println("========== MvcController initial -> initial.jsp");
        return "initial";
    }

}
