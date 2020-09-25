package cn.nihility.cloud.eureka.controller;

import cn.nihility.cloud.eureka.domain.ResultResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * RestTemplate 方式访问服务实现
 */
@RestController
@RequestMapping("/rest")
public class RestTemplateController {

    private static final Logger log = LoggerFactory.getLogger(RestTemplateController.class);
    /* "http://cloud-service-provider" "http://localhost:41100" */
    private static final String REST_PER_SERVICE = "http://cloud-service-provider";

    /* 若要使用自动服务发现转发，就要在 RestTemplate Bean 生成时加上 @LoadBalanced 注解
    * 就可以不用在指定请求服务的具体 IP 地址，默认使用的时 eureka 携带的 ribbon，但它还处于维护阶段
    * 默认使用 ribbon， 没有 ribbon 使用 cloud loadbalancer
    * spring.cloud.loadbalancer.ribbon.enabled=false 禁用 ribbon
    *  */
    private final RestTemplate restTemplate;

    public RestTemplateController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @RequestMapping(value = "/id", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> id() {
        String threadName = Thread.currentThread().getName();
        log.info("the current thread [{}]", threadName);
        Map<String, Object> ret = new HashMap<>(4);
        ret.put("status", "success");
        ret.put("thread", threadName);
        return ret;
    }

    @GetMapping(path = {"/uri"})
    public Map<String, Object> serviceUriByRest() {
        Map<String, Object> ret = new HashMap<>(8);
        String threadName = Thread.currentThread().getName();
        log.info("serviceUriByRest, threadName [{}]", threadName);

        String uri = REST_PER_SERVICE + "/common/id";
        String info = restTemplate.getForObject(uri, String.class);
        log.info("request uri [{}], result [{}]", uri, info);

        ret.put("thread", threadName);
        ret.put("url", uri);
        ret.put("result", info);
        return ret;
    }

    @RequestMapping(value = "/employees", method = RequestMethod.GET)
    public ResultResponse getEmployees() {
        String uri = REST_PER_SERVICE + "/employee/employees";
        log.info("getEmployees, uri [{}]", uri);

        ResultResponse forObject = restTemplate.getForObject(uri, ResultResponse.class);
        log.info("result info [{}]", forObject);
        return forObject;
    }

    private <R> R processUri(BiFunction<RestTemplate, String, R> function, String uri) {
        String url = REST_PER_SERVICE + uri;
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
