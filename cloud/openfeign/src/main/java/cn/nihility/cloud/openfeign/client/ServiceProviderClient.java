package cn.nihility.cloud.openfeign.client;

import cn.nihility.cloud.openfeign.config.FeignRequestConfiguration;
import cn.nihility.common.pojo.UnifyResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(value = "CloudEurekaClient",
    configuration = FeignRequestConfiguration.class,
    fallback = ServiceProviderClientFallback.class)
public interface ServiceProviderClient {

    @RequestMapping("/service/echo/{msg}")
    UnifyResult<String> echo(@PathVariable("msg") String msg);

    @RequestMapping("/service/random/timeout")
    UnifyResult<String> randomTimeOut();

}
