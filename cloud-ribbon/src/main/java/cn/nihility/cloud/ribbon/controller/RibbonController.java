package cn.nihility.cloud.ribbon.controller;

import cn.nihility.cloud.ribbon.domain.Employee;
import cn.nihility.cloud.ribbon.domain.ResultResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Ribbon 采用的是 RestTemplate 来处理连接， Ribbon 负载负载均衡
 */
@RestController
@RequestMapping("/ribbon")
public class RibbonController {

    private static final Logger log = LoggerFactory.getLogger(RibbonController.class);
    private static final String REST_PER_SERVER = "http://cloud-service-provider";

    /* RestTemplate 对服务的消费访问和负载均衡，处理 restful 服务接口 */
    private final RestTemplate restTemplate;
    private final String serviceTag;
    private final Integer localPort;
    private final String mark;

    public RibbonController(RestTemplate restTemplate,
                            @Value("${server.tag}") String serviceTag,
                            @Value("${server.port}") Integer localPort) {
        this.restTemplate = restTemplate;
        this.serviceTag = serviceTag;
        this.localPort = localPort;
        this.mark = serviceTag + ":" + localPort;
    }

    @RequestMapping(value = "/id", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> id() {
        String threadName = Thread.currentThread().getName();
        log.info("service tag [{}], current thread [{}]", mark, threadName);
        Map<String, Object> ret = new HashMap<>(4);
        ret.put("mark", mark);
        ret.put("thread", threadName);
        return ret;
    }

    /* RestTemplate 常用方式
        restTemplate.getForEntity ： 返回响应信息
        restTemplate.getForObject ： 不需要关注 body 外的其它内容，会直接返回响应体的 body 内容并进行对象封装
     */

    @RequestMapping(value = "/common/id", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> commonId() {
        ResponseEntity<String> forEntity = restTemplate.getForEntity(REST_PER_SERVER + "/common/id", String.class);
        log.info("result info status [{}], body [{}]", forEntity.getStatusCodeValue(), forEntity.getBody());
        return forEntity;
    }

    @RequestMapping(value = "/employee/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResultResponse> getEmployeeById(@PathVariable() String id) {
        log.info("getEmployeeById id [{}]", id);

        Map<String, String> requestParams = new HashMap<>(4);
        requestParams.put("id", id);
        ResponseEntity<ResultResponse> entity =
                restTemplate.getForEntity(REST_PER_SERVER + "/employee/{id}",
                        ResultResponse.class, requestParams);
        log.info("result info status [{}], body [{}]", entity.getStatusCode(), entity.getBody());
        return entity;
    }

    @RequestMapping(path = {"/employee/{id}"}, method = RequestMethod.POST)
    public ResultResponse addEmployeeById(@PathVariable("id") String id) {
        log.info("addEmployeeById id [{}]", id);

        Map<String, String> requestParams = new HashMap<>(4);
        requestParams.put("id", id);
        ResultResponse response = restTemplate.postForObject(REST_PER_SERVER + "/employee/{id}",
                null, ResultResponse.class, requestParams);

        log.info("response info [{}]", response);
        return response;
    }

    @RequestMapping(path = {"/employee/delete/{id}"}, method = RequestMethod.POST)
    public ResultResponse deleteEmployeeById(@PathVariable("id") Integer id) {
        log.info("deleteEmployeeById [{}]", id);

        ResultResponse response = restTemplate.postForObject(REST_PER_SERVER + "/employee/delete/{id}",
                null, ResultResponse.class, id);

        log.info("response info [{}]", response);
        return response;
    }

    @RequestMapping(path = {"/employee/add/{id}"}, method = RequestMethod.POST)
    public ResultResponse addEmployeeById(@PathVariable("id") Integer id) {

        Employee employee = Employee.build(id);
        log.info("addEmployeeById [{}], employee [{}]", id, employee);

        ResultResponse response = restTemplate.postForObject(REST_PER_SERVER + "/employee/add",
                employee, ResultResponse.class);

        log.info("response info [{}]", response);
        return response;
    }

}
