package cn.nihility.cloud.eureka.controller;

import cn.nihility.common.pojo.UnifyResult;
import cn.nihility.common.util.UnifyResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
public class ServiceProviderController {

    private static final Logger logger = LoggerFactory.getLogger(ServiceProviderController.class);

    @RequestMapping("/service/echo/{msg}")
    public UnifyResult echo(@PathVariable String msg) {
        logger.info("Echo [{}]", msg);
        return UnifyResultUtil.success(msg);
    }

    @RequestMapping("/service/random/timeout")
    public UnifyResult randomTimeOut() {
        Random random = new Random(System.currentTimeMillis());
        int duration = random.nextInt(10);
        logger.info("randomTimeOut [{}] s", duration);
        try {
            Thread.sleep(duration * 1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return UnifyResultUtil.success("randomTimeOut [" + duration + "]");
    }

}
