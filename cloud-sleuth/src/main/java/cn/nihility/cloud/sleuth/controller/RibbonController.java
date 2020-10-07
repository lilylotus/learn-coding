package cn.nihility.cloud.sleuth.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sleuth")
public class RibbonController {

    private static final Logger log = LoggerFactory.getLogger(RibbonController.class);
    private final RibbonOpenFeign ribbonOpenFeign;

    public RibbonController(RibbonOpenFeign ribbonOpenFeign) {
        this.ribbonOpenFeign = ribbonOpenFeign;
    }

    @RequestMapping("/employee/{id}")
    public ResponseEntity<Object> findEmployeeById(@PathVariable Integer id) {
        log.info("employee id [{}]", id);
        return ribbonOpenFeign.getEmployeeById(id);
    }

    @RequestMapping("/id")
    public ResponseEntity<Object> id() {
        log.info("id");
        return ResponseEntity.ok().body(ribbonOpenFeign.id());
    }

}
