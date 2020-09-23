package cn.nihility.cloud.controller;

import cn.nihility.cloud.service.ServiceConstant;
import cn.nihility.cloud.uitil.SnowFlakeId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 常用功能实现 web 接口
 */
@RestController
@RequestMapping("/common")
public class CommonServiceController {

    private static final Logger log = LoggerFactory.getLogger(CommonServiceController.class);

    private final ServiceConstant constant;

    public CommonServiceController(ServiceConstant constant) {
        this.constant = constant;
    }

    @RequestMapping(value = "/id", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> id() {
        String threadName = Thread.currentThread().getName();
        log.info("service tag [{}], current thread [{}]", constant.tag(), threadName);
        Map<String, Object> ret = new HashMap<>(8);
        ret.put("status", "success");
        ret.put("id", SnowFlakeId.nextSnowId());
        ret.put("tag", constant.tag());
        ret.put("thread", threadName);
        return ret;
    }

}
