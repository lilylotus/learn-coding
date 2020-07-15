package cn.nihility.cloud.controller;

import cn.nihility.cloud.command.EmployeeCommand;
import cn.nihility.cloud.entity.Employee;
import lombok.extern.slf4j.Slf4j;
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
 * 原始的 ip 固定方式调用
 *
 * @author dandelion
 * @date 2020-04-12 23:21
 */
@RestController
@RequestMapping("/origin")
@Slf4j
public class EmployeeOriginController {

    private static final String PER_SERVICE = "spring-cloud-service-provider";
    private static final String IP_SERVICE = "http://spring-cloud-service-provider";
    private static final String REST_PER_SERVICE = "http://localhost:52200";

    @RequestMapping(path = {"/tag"}, method = RequestMethod.GET)
    public String serviceTag() {
        String threadName = Thread.currentThread().getName();
        log.info("EmployeeOriginController -> serviceTag, threadName [{}]", threadName);
        return "tag -> " + threadName;
    }
    

    @RequestMapping(path = {"/restUri"}, method = RequestMethod.GET)
    public String serviceUriByRest() {
        RestTemplate restTemplate = new RestTemplate();
        String threadName = Thread.currentThread().getName();
        log.info("EmployeeOriginController -> serviceUriByRest, threadName [{}]", threadName);
        String uri = REST_PER_SERVICE + "/employee/tag";
        log.info("remote uri [{}]", uri);
        String tag = restTemplate.getForObject(uri, String.class);
        log.info("tag [{}]", tag);

        return uri + " : tag -> " + tag + " -> " + threadName;
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public List<Employee> getEmployees() {
        RestTemplate restTemplate = new RestTemplate();
        log.info("EmployeeOriginController -> getEmployees");
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
        RestTemplate restTemplate = new RestTemplate();
        String url = REST_PER_SERVICE + uri;
        log.info("remote url [{}]", url);
        return function.apply(restTemplate, url);
    }

   @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Employee getEmployeeById(@PathVariable("id") Integer id) {
       String threadName = Thread.currentThread().getName();
        log.info("EmployeeOriginController -> getEmployeeById id [{}], threadName [{}]", id, threadName);

       /*RestTemplate restTemplate = new RestTemplate();
       String url = REST_PER_SERVICE + "/employee/{id}";
       final Employee employee = restTemplate.getForObject(url, Employee.class, id);*/
       /*Employee employee = process(((r, v) -> r.getForObject(v, Employee.class)), "/employee/" + id);*/
       /**
        * 使用 EmployeeCommand 调用远程服务
        */
       EmployeeCommand employeeCommand = new EmployeeCommand(new RestTemplate(), (long) id);
       final Employee employee = employeeCommand.execute();
       log.info("employee [{}]", employee);

        return employee;
    }

    @RequestMapping(path = {"/add/{id}"}, method = RequestMethod.POST)
    public Employee addEmployeeById(@PathVariable("id") Integer id) {
        log.info("EmployeeOriginController -> addEmployeeById id [{}]", id);

        Employee employee = process(((r, v) -> r.postForObject(v, null, Employee.class, id)), "/employee/add/{id}");
        log.info("employee [{}]", employee);

        return employee;
    }

}
