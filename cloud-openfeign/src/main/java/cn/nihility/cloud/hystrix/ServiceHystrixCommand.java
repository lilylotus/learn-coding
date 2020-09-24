package cn.nihility.cloud.hystrix;

import cn.nihility.cloud.domain.Employee;
import cn.nihility.cloud.domain.ResultResponse;
import com.netflix.hystrix.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

public class ServiceHystrixCommand extends HystrixCommand<ResultResponse> {

    private static final Logger log = LoggerFactory.getLogger(ServiceHystrixCommand.class);
    private final RestTemplate restTemplate;
    private final Integer id;

    public ServiceHystrixCommand(RestTemplate restTemplate, Integer id) {
        super(setter());
        this.restTemplate = restTemplate;
        this.id = id;
    }

    private static Setter setter() {

        // 服务分组
        final HystrixCommandGroupKey groupKey = HystrixCommandGroupKey.Factory.asKey("employee_service");
        // 服务标示
        final HystrixCommandKey commandKey = HystrixCommandKey.Factory.asKey("employee");
        // 线程池名称
        final HystrixThreadPoolKey threadPoolKey = HystrixThreadPoolKey.Factory.asKey("employee_service_pool");

        /*
         * 线程池配置：
         *      withCoreSize : 核心线程数量 10
         *      withKeepAliveTimeMinutes： 线程存活时间
         *      withQueueSizeRejectionThreshold： 队列等待阈值 100， 超过 100 拒绝
         */
        final HystrixThreadPoolProperties.Setter threadPoolProperties =
                HystrixThreadPoolProperties.Setter().withCoreSize(50)
                        .withKeepAliveTimeMinutes(15)
                        .withQueueSizeRejectionThreshold(100);

        // 命令属性配置 Hystrix 开启超时
        /*
         *  withExecutionIsolationStrategy : 采用线程池方式实现隔离
         *  withExecutionTimeoutEnabled ： 禁止
         */
        final HystrixCommandProperties.Setter commandProperties = HystrixCommandProperties.Setter()
                .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.THREAD)
                .withExecutionTimeoutEnabled(false);

        return Setter.withGroupKey(groupKey).andCommandKey(commandKey).andThreadPoolKey(threadPoolKey)
                .andThreadPoolPropertiesDefaults(threadPoolProperties).andCommandPropertiesDefaults(commandProperties);
    }

    @Override
    protected ResultResponse run() {
        log.info("execute run id [{}] , threadName [{}]", id, Thread.currentThread().getName());
        return restTemplate.getForObject("http://localhost:41100/employee/{id}", ResultResponse.class, id);
    }

    @Override
    protected ResultResponse getFallback() {
        return ResultResponse.success(new Employee(0, "Hystrix Fallback", "Hystrix Fallback")).withMessage("Hystrix Command");
    }
}
