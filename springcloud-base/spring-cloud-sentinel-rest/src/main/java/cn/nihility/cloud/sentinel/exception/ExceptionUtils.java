package cn.nihility.cloud.sentinel.exception;

import cn.nihility.cloud.sentinel.entity.Employee;
import com.alibaba.cloud.sentinel.rest.SentinelClientHttpResponse;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;

/**
 * ExceptionUtils
 *
 * @author dandelion
 * @date 2020-04-26 18:52
 */
public class ExceptionUtils {

    private static final Logger log = LoggerFactory.getLogger(ExceptionUtils.class);

    public static SentinelClientHttpResponse handleFallBack(HttpRequest request, byte[] body,
                                                            ClientHttpRequestExecution execution,
                                                            BlockException ex) {
        log.error("ExceptionUtils -> handleBlockHandler 熔断异常降级", ex);
        Employee employee = new Employee(0, "熔断异常降级", "熔断异常降级", "熔断异常降级");
        return new SentinelClientHttpResponse(JSON.toJSONString(employee));
    }

    public static SentinelClientHttpResponse handleBlockHandler(HttpRequest request, byte[] body,
                                                                ClientHttpRequestExecution execution,
                                                                BlockException ex) {
        log.error("ExceptionUtils -> handleBlockHandler 熔断限流降级", ex);
        Employee employee = new Employee(0, "熔断限流降级", "熔断限流降级", "熔断限流降级");
        return new SentinelClientHttpResponse(JSON.toJSONString(employee));
    }

}
