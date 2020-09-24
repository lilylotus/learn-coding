package cn.nihility.cloud.config;

import feign.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class OpenFeignConfiguration {

    /**
     * NONE: 默认的，不显示任何日志;
     * BASIC: 仅记录请求方法、URL、 响应状态码及执行时间;
     * HEADERS: 除了BASIC 中定义的信息之外，还有请求和响应的头信息;
     * FULL: 除了HEADERS中定义的信息之外,还有请求和响应的正文及元数据。
     */
    @Bean
    public Logger.Level openFeignLoggerLevel() {
        return Logger.Level.FULL;
    }


    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    /*@Bean
    public ServletRegistrationBean<HystrixMetricsStreamServlet> servletRegistrationBean(){
        ServletRegistrationBean<HystrixMetricsStreamServlet> registrationBean =
                new ServletRegistrationBean(new HystrixMetricsStreamServlet());
        registrationBean.setLoadOnStartup(1);
        registrationBean.addUrlMappings("/actuator/hystrix.stream");
        registrationBean.setName("HystrixMetricsStreamServlet");
        return registrationBean;
    }*/

}
