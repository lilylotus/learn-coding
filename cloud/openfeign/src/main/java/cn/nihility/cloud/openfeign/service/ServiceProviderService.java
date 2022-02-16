package cn.nihility.cloud.openfeign.service;

import cn.nihility.cloud.openfeign.client.ServiceProviderClient;
import cn.nihility.common.pojo.UnifyResult;
import cn.nihility.common.util.UnifyResultUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ServiceProviderService {

    private static final Logger logger = LoggerFactory.getLogger(ServiceProviderService.class);

    private ServiceProviderClient serviceProviderClient;

    public ServiceProviderService(ServiceProviderClient serviceProviderClient) {
        this.serviceProviderClient = serviceProviderClient;
    }

    public UnifyResult<UnifyResult<String>> echo(String msg) {
        return UnifyResultUtils.success(serviceProviderClient.echo(msg));
    }

    public UnifyResult<String> randomTimeOut() {
        UnifyResult<String> result = serviceProviderClient.randomTimeOut();
        return null != result ? UnifyResultUtils.success(result.getData()) : UnifyResultUtils.success("empty");
    }

    public UnifyResult<Object> circuitBreakerWithException() {
        throw new IllegalArgumentException("circuitBreakerWithException");
    }

    public UnifyResult<Object> slowBulkhead() {
        throw new IllegalArgumentException("circuitBreakerWithException");
    }

    public UnifyResult<Object> circuitBreakerWithExceptionFallback(Throwable ex) {
        logger.error("CircuitBreaker slowRandomTimeOut fallback", ex);
        return UnifyResultUtils.success("slowRandomTimeOut [" + ex.getMessage() + "]");
    }

    public UnifyResult<Object> randomTimeOutFallback(Throwable ex) {
        logger.error("CircuitBreaker randomTimeOut fallback", ex);
        return UnifyResultUtils.success("randomTimeOut [" + ex.getMessage() + "]");
    }

    public UnifyResult<Object> slowBulkheadFallback(Throwable ex) {
        logger.error("CircuitBreaker slowBulkhead fallback", ex);
        return UnifyResultUtils.success("slowBulkhead [" + ex.getMessage() + "]");
    }

}
