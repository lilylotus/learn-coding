package cn.nihility.cloud.eureka.controller;

import cn.nihility.cloud.eureka.domain.ResultResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * RestTemplate 带服务注册发现方式访问服务实现
 * 采用 客户端负载均衡 Ribbon 实现
 */
@RestController
@RequestMapping("/ribbon")
public class RestTemplateDiscoveryController {

    private static final Logger log = LoggerFactory.getLogger(RestTemplateDiscoveryController.class);

    private static final String EUREKA_REGISTRY_SERVICE = "cloud-service-provider";
    private static final String REST_PER_SERVICE = "http://cloud-service-provider";

    /* 使用 RestTemplate + @LoadBalance 注解可实现自动负载，不用 LoadBalancerClient */
    private static final RestTemplate restTemplate = new RestTemplate();
    /* 使用 Ribbon 的 LoadBalancerClient，做服务发现 */
    private final LoadBalancerClient loadBalancerClient;

    private final String serviceTag;
    private final Integer localPort;

    public RestTemplateDiscoveryController(LoadBalancerClient loadBalancerClient,
                                           @Value("${server.tag}") String serviceTag,
                                           @Value("${server.port}") Integer localPort) {
        this.loadBalancerClient = loadBalancerClient;
        this.serviceTag = serviceTag;
        this.localPort = localPort;
    }

    @RequestMapping(value = "/id", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> id() {
        String threadName = Thread.currentThread().getName();
        log.info("service tag [{}], current thread [{}]", serviceTag, threadName);
        Map<String, Object> ret = new HashMap<>(4);
        ret.put("tag", serviceTag + ":" + localPort);
        ret.put("thread", threadName);
        return ret;
    }

    /**
     * 使用 spring cloud loadbalancer 的方式，手动获取服务地址配置
     */
    private String loadBalanceClientHost() {
        ServiceInstance instance = loadBalancerClient.choose(EUREKA_REGISTRY_SERVICE);
        String schema = instance.getScheme();
        String host = instance.getHost();
        int port = instance.getPort();
        log.info("scheme [{}], host [{}], port [{}]", schema, host, port);
        return "http://" + host + ":" + port;
    }

    @GetMapping(path = {"/uri"})
    public Map<String, Object> serviceUriByRest() {
        Map<String, Object> ret = new HashMap<>(8);
        String threadName = Thread.currentThread().getName();
        log.info("serviceUriByRest, threadName [{}]", threadName);

        String uri = loadBalanceClientHost() + "/common/id";
        String info = restTemplate.getForObject(uri, String.class);
        log.info("request uri [{}], result [{}]", uri, info);

        ret.put("thread", threadName);
        ret.put("url", uri);
        ret.put("result", info);
        return ret;
    }

    @RequestMapping(value = "/employees", method = RequestMethod.GET)
    public ResultResponse getEmployees() {
        String uri = loadBalanceClientHost() + "/employee/employees";
        log.info("getEmployees, uri [{}]", uri);

        ResultResponse forObject = restTemplate.getForObject(uri, ResultResponse.class);
        log.info("result info [{}]", forObject);
        return forObject;
    }

    private <R> R processUri(BiFunction<RestTemplate, String, R> function, String uri) {
        String url = loadBalanceClientHost() + uri;
        log.info("remote url [{}]", url);
        return function.apply(restTemplate, url);
    }

    @RequestMapping(value = "/employee/{id}", method = RequestMethod.GET)
    public ResultResponse getEmployeeById(@PathVariable("id") Integer id) {
        String threadName = Thread.currentThread().getName();
        log.info("getEmployeeById id [{}], threadName [{}]", id, threadName);

        /*String url = REST_PER_SERVICE + "/employee/{id}";
        Employee employee = restTemplate.getForObject(url, Employee.class, id);*/

        ResultResponse result = processUri(((r, v) -> r.getForObject(v, ResultResponse.class)), "/employee/" + id);

        /**
         * 使用 EmployeeCommand 调用远程服务
         */
        /*EmployeeCommand employeeCommand = new EmployeeCommand(restTemplate, (long) id);
        final Employee employee = employeeCommand.execute();
        log.info("employee [{}]", employee);*/

        log.info("result info [{}]", result);
        return result;
    }

    @RequestMapping(path = {"/employee/{id}"}, method = RequestMethod.POST)
    public ResultResponse addEmployeeById(@PathVariable("id") Integer id) {
        log.info("addEmployeeById [{}]", id);

        ResultResponse result = processUri(((r, v) -> r.postForObject(v, null, ResultResponse.class, id)), "/employee/{id}");
        log.info("result info [{}]", result);
        return result;
    }

}
