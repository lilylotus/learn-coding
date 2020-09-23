package cn.nihility.cloud.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 一些 service 常量
 */
@Component
public class ServiceConstant {

    private final String serviceTag;
    private final Integer localPort;

    public ServiceConstant(@Value("${server.tag}") String serviceTag, @Value("${server.port}") Integer localPort) {
        this.serviceTag = serviceTag;
        this.localPort = localPort;
    }


    public String tag() {
        return serviceTag + ":" + localPort;
    }

}
