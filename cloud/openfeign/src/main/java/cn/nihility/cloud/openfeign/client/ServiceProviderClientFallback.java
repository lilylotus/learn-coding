package cn.nihility.cloud.openfeign.client;

import cn.nihility.common.pojo.UnifyResult;
import cn.nihility.common.util.UnifyResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ServiceProviderClientFallback implements ServiceProviderClient {

    private static final Logger logger = LoggerFactory.getLogger(ServiceProviderClientFallback.class);

    @Override
    public UnifyResult echo(String msg) {
        return UnifyResultUtil.success("Fallback [" + msg + "]");
    }

    @Override
    public UnifyResult randomTimeOut() {
        logger.warn("CircuitBreaker randomTimeOut");
        return UnifyResultUtil.success(-1);
    }

}
