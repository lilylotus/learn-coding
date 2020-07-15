package cn.nihility.cloud.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * FeignController
 *
 * @author dandelion
 * @date 2020-04-11 12:09
 */
@RestController
@RequestMapping("/feign")
public class FeignController {

    @Autowired
    private HelloClient client;

    @RequestMapping(value = {"/"}, method = RequestMethod.GET)
    public String root() {
        return client.root();
    }

    @RequestMapping(value = {"/hello"}, method = RequestMethod.GET)
    public String hello() {
        return client.hello();
    }

    @RequestMapping(value = {"/hei"}, method = RequestMethod.GET)
    public String hei() {
        return client.hei();
    }

    @FeignClient("cloud-load-balancer")
    interface HelloClient {
        @RequestMapping(value = "/", method = RequestMethod.GET)
        String root();

        @RequestMapping(value = "/hello", method = RequestMethod.GET)
        String hello();

        @RequestMapping(value = "/hi", method = RequestMethod.GET)
        String hei();

    }

}
