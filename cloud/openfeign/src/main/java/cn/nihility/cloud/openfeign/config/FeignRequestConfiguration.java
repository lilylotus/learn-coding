package cn.nihility.cloud.openfeign.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;

@Component
public class FeignRequestConfiguration implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {

        template.header("Feign-Attach", "Feign attach test value");

    }

}
