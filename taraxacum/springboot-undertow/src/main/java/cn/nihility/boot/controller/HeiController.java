package cn.nihility.boot.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author dandelion
 * @date 2020-09-20 14:05
 */
@RestController
@RequestMapping("/hei")
public class HeiController {

    private static final Logger log = LoggerFactory.getLogger(HeiController.class);

    @GetMapping("/hei")
    public Map<String, Object> hei() {
        log.info("HeiController hei");
        Map<String, Object> map = new HashMap<>();
        map.put("requestBody", "1");
        map.put("requestEntity", "2");
        return map;
    }

}
