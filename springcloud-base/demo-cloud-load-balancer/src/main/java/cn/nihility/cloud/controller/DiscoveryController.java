package cn.nihility.cloud.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Random;

/**
 * DiscoveryController
 *
 * @author dandelion
 * @date 2020-04-11 11:58
 */
@RestController
public class DiscoveryController {

    @Autowired
    private DiscoveryClient client;

    @RequestMapping("/")
    public String hello() {
        final List<ServiceInstance> instances = client.getInstances("cloud-load-balancer");
        final ServiceInstance serviceInstance = instances.get(new Random().nextInt(instances.size()));
        return "Hello World : " + serviceInstance.getServiceId() + ":" +
                serviceInstance.getHost() + ":" +
                serviceInstance.getPort();
    }

}
