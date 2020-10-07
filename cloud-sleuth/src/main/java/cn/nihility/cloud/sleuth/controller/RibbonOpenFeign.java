package cn.nihility.cloud.sleuth.controller;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(value = "cloud-ribbon", fallback = RibbonOpenFeignFallback.class)
public interface RibbonOpenFeign {

    @RequestMapping(value = "/ribbon/employee/{id}")
    ResponseEntity<Object> getEmployeeById(@PathVariable("id") Integer id);

    @RequestMapping(value = "/ribbon/id")
    ResponseEntity<Object> id();

}
