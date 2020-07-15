package cn.nihility.use.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * DemoController
 *
 * @author dandelion
 * @date 2020-04-12 11:03
 */
@RestController
@RequestMapping("/demo")
public class DemoController {

    @RequestMapping(value = "/hei", method = RequestMethod.GET)
    public String hei() {
        return "Hei";
    }

}
