package cn.nihility.cloud.openfeign.controller;

import cn.nihility.cloud.openfeign.service.ServiceProviderService;
import cn.nihility.common.pojo.UnifyResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ServiceConsumerController {

    private static final Logger logger = LoggerFactory.getLogger(ServiceConsumerController.class);

    private ServiceProviderService providerService;

    public ServiceConsumerController(ServiceProviderService providerService) {
        this.providerService = providerService;
    }

    @RequestMapping("/consumer/echo/{msg}")
    public UnifyResult<UnifyResult<String>> echo(@PathVariable("msg") String msg) {
        logger.info("Echo  [{}]", msg);
        UnifyResult<UnifyResult<String>> result = providerService.echo(msg);
        logger.info("Echo result [{}]", result);
        return result;
    }

    @RequestMapping("/consumer/random/timeout")
    public UnifyResult<UnifyResult<String>> randomTimeOut() {
        UnifyResult<UnifyResult<String>> result = providerService.randomTimeOut();
        logger.info("RandomTimeOut result [{}]", result);
        return result;
    }

    @RequestMapping("/consumer/exception")
    public UnifyResult<Object> circuitBreakerWithException() {
        return providerService.circuitBreakerWithException();
    }

}
