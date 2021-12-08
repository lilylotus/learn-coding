package cn.nihility.cloud.openfeign.client;

import cn.nihility.common.pojo.UnifyResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(value = "CloudEurekaClient",
    configuration = ServiceProviderClientConfiguration.class,
    fallback = ServiceProviderClientFallback.class)
public interface ServiceProviderClient {

    @RequestMapping("/service/echo/{msg}")
    UnifyResult echo(@PathVariable("msg") String msg);

    @RequestMapping("/service/random/timeout")
    UnifyResult randomTimeOut();

}
