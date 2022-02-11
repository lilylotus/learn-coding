package cn.nihility.cloud.openfeign.service;

import cn.nihility.cloud.openfeign.client.ServiceProviderClient;
import cn.nihility.common.pojo.UnifyResult;
import cn.nihility.common.util.UnifyResultUtil;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
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
        return UnifyResultUtil.success(serviceProviderClient.echo(msg));
    }

    @CircuitBreaker(name = "randomTimeOut", fallbackMethod = "randomTimeOutFallback")
    public UnifyResult<UnifyResult<String>> randomTimeOut() {
        return UnifyResultUtil.success(serviceProviderClient.randomTimeOut());
    }

    @CircuitBreaker(name = "slowRandomTimeOut", fallbackMethod = "circuitBreakerWithExceptionFallback")
    public UnifyResult<Object> circuitBreakerWithException() {
        throw new IllegalArgumentException("circuitBreakerWithException");
    }

    @CircuitBreaker(name = "slowBulkhead", fallbackMethod = "slowBulkheadFallback")
    public UnifyResult<Object> slowBulkhead() {
        throw new IllegalArgumentException("circuitBreakerWithException");
    }

    public UnifyResult<Object> circuitBreakerWithExceptionFallback(Throwable ex) {
        logger.error("CircuitBreaker slowRandomTimeOut fallback", ex);
        return UnifyResultUtil.success("slowRandomTimeOut [" + ex.getMessage() + "]");
    }

    public UnifyResult<Object> randomTimeOutFallback(Throwable ex) {
        logger.error("CircuitBreaker randomTimeOut fallback", ex);
        return UnifyResultUtil.success("randomTimeOut [" + ex.getMessage() + "]");
    }

    public UnifyResult<Object> slowBulkheadFallback(Throwable ex) {
        logger.error("CircuitBreaker slowBulkhead fallback", ex);
        return UnifyResultUtil.success("slowBulkhead [" + ex.getMessage() + "]");
    }

}
