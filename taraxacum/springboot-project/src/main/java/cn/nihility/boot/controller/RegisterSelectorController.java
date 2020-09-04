package cn.nihility.boot.controller;

import cn.nihility.boot.registrar.mapper.DuckDao;
import cn.nihility.boot.selector.Zoom;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/register")
public class RegisterSelectorController {

    private final DuckDao duckDao;
    private final Zoom zoom;

    public RegisterSelectorController(DuckDao duckDao, Zoom zoom) {
        this.duckDao = duckDao;
        this.zoom = zoom;
    }

    @RequestMapping("/selector")
    public Map<String, Object> selector() {
        Map<String, Object> ret = new HashMap<>(8);
        ret.put("ImportBeanDefinitionRegistrar", duckDao.findDuckById(1));
        ret.put("ImportSelector", zoom.toString());
        ret.put("zoom", zoom);
        return ret;
    }
}
