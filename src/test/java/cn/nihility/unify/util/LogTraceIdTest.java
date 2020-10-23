package cn.nihility.unify.util;

import cn.nihility.unify.pojo.UnifyResult;
import cn.nihility.unify.wrapper.ThreadPoolExecutorWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

public class LogTraceIdTest {

    private static final Logger log = LoggerFactory.getLogger(LogTraceIdTest.class);

    @Test
    public void testExecThreadTraceId() {
        ThreadPoolExecutorWrapper executorWrapper =
                new ThreadPoolExecutorWrapper(6, 10, 10, TimeUnit.SECONDS, new SynchronousQueue<>());

        executorWrapper.execute(() -> {
            log.info("Log TraceId Test, Thread Name [{}]", Thread.currentThread().getName());
        });

        executorWrapper.execute(() -> {
            log.info("Log TraceId Test, Thread Name [{}]", Thread.currentThread().getName());
        });
        executorWrapper.execute(() -> {
            log.info("Log TraceId Test, Thread Name [{}]", Thread.currentThread().getName());
        });

        executorWrapper.execute(() -> {
            log.info("Log TraceId Test, Thread Name [{}]", Thread.currentThread().getName());
        });

        executorWrapper.execute(() -> {
            log.info("Log TraceId Test, Thread Name [{}]", Thread.currentThread().getName());
        });
    }

    @Test
    public void testHttpClientRequestTrace() {
        String url = "http://127.0.0.1:49000/v1/hei/success";
        String result = HttpClientUtil.doGet(url);
        log.info("HttpClient DoGet Result [{}]", result);
    }

    @Test
    public void testRestTemplateRequestTraceId() {
        String url = "http://127.0.0.1:49000/v1/hei/success";
        try {
            String response = RestTemplateUtil.doGet(url, UnifyResult.class);
            log.info("Rest Template Util Response [{}]", response);
        } catch (JsonProcessingException e) {
            log.error("Request URL [{}] Error", url, e);
        }
    }

}
