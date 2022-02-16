package cn.nihility.cloud.openfeign.client;

import cn.nihility.common.pojo.UnifyResult;
import cn.nihility.common.util.UnifyResultUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ServiceProviderClientFallback implements ServiceProviderClient {

    private static final Logger logger = LoggerFactory.getLogger(ServiceProviderClientFallback.class);

    @Override
    public UnifyResult<String> echo(String msg) {
        return UnifyResultUtils.success("Fallback [" + msg + "]");
    }

    @Override
    public UnifyResult<String> randomTimeOut() {
        logger.warn("CircuitBreaker randomTimeOut");
        return UnifyResultUtils.success("CircuitBreaker randomTimeOut");
    }

}
