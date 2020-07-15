package cn.nihility.cloud.sentinel.controller;

import cn.nihility.cloud.sentinel.entity.Employee;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * EmployeeServiceController
 *
 * @author dandelion
 * @date 2020-04-12 23:21
 */
@RestController
@RequestMapping("/employee")
@Slf4j
public class EmployeeServiceController {

    private static final String PER_SERVICE = "spring-cloud-service-provider";
    private static final String REST_PER_SERVICE = "http://spring-cloud-service-provider";

    @Autowired
    private RestTemplate restTemplate;

    @RequestMapping(path = {"/restUri"}, method = RequestMethod.GET)
    public String serviceUriByRest() {
        log.info("EmployeeServiceController -> serviceUriByRest");
        String uri = REST_PER_SERVICE + "/employee/tag";
        log.info("remote uri [{}]", uri);
        String tag = restTemplate.getForObject(uri, String.class);
        log.info("tag [{}]", tag);

        return uri + " : tag -> " + tag;
    }

    public String fallbackMethod() {
        log.info("EmployeeServiceController -> fallbackMethod");
        return "Fallback response:: No services details available temporarily";
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public List<Employee> getEmployees() {
        log.info("EmployeeServiceController -> getEmployees");
        String uri = REST_PER_SERVICE + "/employee/list";
        log.info("remote uri [{}]", uri);
        Employee[] employees = restTemplate.getForObject(uri, Employee[].class);

        if (null != employees) {
            log.info("remote info [{}]", Arrays.stream(employees).map(Objects::toString).collect(Collectors.joining("|")));
            return Arrays.asList(employees);
        } else {
            return new ArrayList<>();
        }
    }

    private <R> R process(BiFunction<RestTemplate, String, R> function, String uri) {
        String url = REST_PER_SERVICE + uri;
        log.info("remote url [{}]", url);
        return function.apply(restTemplate, url);
    }

    /**
     * @SentinelResource
     *      blockHandler: 熔断降级的调用方法
     *      fallback: 抛出异常的降级方法
     */
//    @SentinelResource(value = "getEmployeeById", blockHandler = "serviceBlockHandler", fallback = "serviceFallBack")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Employee getEmployeeById(@PathVariable("id") Integer id) {
        log.info("EmployeeServiceController -> getEmployeeById id [{}]", id);

        if (10 == id) {
            throw new RuntimeException("请求异常，触发熔断");
        }

        Employee employee = process(((r, v) -> r.getForObject(v, Employee.class)), "/employee/" + id);
        log.info("employee [{}]", employee);

        return employee;
    }

    /**
     * 定义 sentinel 的降级逻辑
     * 熔断降级执行方法
     * 抛出异常执行降级方法
     */
    public Employee serviceBlockHandler(Integer id) {
        return new Employee(id, "Sentinel RestTemplate 熔断降级",
                "Sentinel RestTemplate 熔断降级", "Sentinel RestTemplate 熔断降级");
    }
    public Employee serviceFallBack(Integer id) {
        return new Employee(id, "Sentinel RestTemplate 异常降级",
                "Sentinel RestTemplate 异常降级", "Sentinel RestTemplate 异常降级");
    }

    /**
     *  Hystrix 降级的方法和需要保护的方法返回值类型一致
     */
    public Employee fallbackIdMethod(Integer id) {
        log.info("EmployeeFeignController -> fallbackMethod id [{}]", id);

        Employee employee = new Employee();
        employee.setId(id);
        employee.setAddress("hystrix 熔断降级");
        employee.setDept("failure，hystrix 熔断降级");
        employee.setName("failure，hystrix 熔断降级");

        return employee;
    }

    @RequestMapping(path = {"/add/{id}"}, method = RequestMethod.POST)
    public Employee addEmployeeById(@PathVariable("id") Integer id) {
        log.info("EmployeeServiceController -> addEmployeeById id [{}]", id);

        Employee employee = process(((r, v) -> r.postForObject(v, null, Employee.class, id)), "/employee/add/{id}");
        log.info("employee [{}]", employee);

        return employee;
    }

}
