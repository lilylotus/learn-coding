package cn.nihility.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * InitializerController
 *
 * @author dandelion
 * @date 2020-03-24 22:47
 */
@Controller
public class InitializerController {

    @RequestMapping(path = {"/app/hei"}, method = RequestMethod.GET)
    public String initial(HttpServletRequest request) {
        System.out.println("======== InitializerController initial()");
        request.setAttribute("param1", "add request value.");
        return "initial";
    }

}
