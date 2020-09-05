package cn.nihility.boot.controller;

import cn.nihility.boot.bean.InjectConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author dandelion
 * @date 2020-09-05 16:42
 */
@RestController
@RequestMapping("/inject")
public class InjectController {

    private final InjectConfiguration injectConfiguration;

    public InjectController(InjectConfiguration injectConfiguration) {
        this.injectConfiguration = injectConfiguration;
    }


    @RequestMapping("/my")
    public Map<String, Object> inject() {
        Map<String, Object> map = new HashMap<>(4);
        map.put("inject-object", injectConfiguration);
        return map;
    }

}
