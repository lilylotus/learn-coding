package cn.nihility.cloud.openfeign.controller;

import cn.nihility.cloud.openfeign.client.ServiceProviderClient;
import cn.nihility.common.pojo.UnifyResult;
import cn.nihility.common.util.UnifyResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ServiceConsumerController {

    private static final Logger logger = LoggerFactory.getLogger(ServiceConsumerController.class);

    private ServiceProviderClient serviceProviderClient;

    public ServiceConsumerController(ServiceProviderClient serviceProviderClient) {
        this.serviceProviderClient = serviceProviderClient;
    }

    @RequestMapping("/consumer/echo/{msg}")
    public UnifyResult echo(@PathVariable("msg") String msg) {
        logger.info("Echo  [{}]", msg);
        UnifyResult result = serviceProviderClient.echo(msg);
        logger.info("Echo result [{}]", result);
        return UnifyResultUtil.success(result);
    }

    @RequestMapping("/consumer/random/timeout")
    public UnifyResult randomTimeOut() {
        UnifyResult result = serviceProviderClient.randomTimeOut();
        logger.info("RandomTimeOut result [{}]", result);
        return UnifyResultUtil.success(result);
    }

}
