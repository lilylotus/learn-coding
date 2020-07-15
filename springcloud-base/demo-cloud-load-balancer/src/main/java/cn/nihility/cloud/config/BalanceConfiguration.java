package cn.nihility.cloud.config;

import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;

/**
 * BalanceConfiguration
 *
 * @author dandelion
 * @date 2020-04-11 00:02
 */
@Configuration
public class BalanceConfiguration {

    @Bean
    @Primary
    public ServiceInstanceListSupplier serviceInstanceSupplier() {
        return new DemoServiceInstanceListSuppler("cloud-load-balancer-client");
    }

}

class DemoServiceInstanceListSuppler implements ServiceInstanceListSupplier {

    private final String serviceId;

    public DemoServiceInstanceListSuppler(String serviceId) {
        this.serviceId = serviceId;
    }

    @Override
    public String getServiceId() {
        return serviceId;
    }

    @Override
    public Flux<List<ServiceInstance>> get() {
        return Flux.just(Arrays
                .asList(new DefaultServiceInstance(serviceId + "1", serviceId, "localhost", 51001, false),
                        new DefaultServiceInstance(serviceId + "2", serviceId, "localhost", 51001, false),
                        new DefaultServiceInstance(serviceId + "3", serviceId, "localhost", 51001, false)));
    }
}
