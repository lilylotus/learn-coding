package cn.nihility.cloud.eureka.controller;

import cn.nihility.cloud.eureka.model.Employee;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
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


    /* 使用 Ribbon 的 LoadBalancerClient
    * */
    @Autowired
    private LoadBalancerClient loadBalancer;
    /* 使用 RestTemplate + @LoadBalance 注解 */
    @Autowired
    private RestTemplate restTemplate;

    @RequestMapping(path = {"/uri"}, method = RequestMethod.GET)
    public String serviceUri() {
        log.info("EmployeeServiceController -> serviceUri");
        ServiceInstance instance = loadBalancer.choose(PER_SERVICE);
        String uri = String.format("http://%s:%s%s", instance.getHost(), instance.getPort(), "/employee/tag");
        log.info("remote uri [{}]", uri);

        RestTemplate restTemplate = new RestTemplate();
        String tag = restTemplate.getForObject(uri, String.class);
        log.info("tag [{}]", tag);

        return uri + " : tag -> " + tag;
    }

    @HystrixCommand(fallbackMethod = "fallbackMethod")
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

    @HystrixCommand(fallbackMethod = "fallbackIdMethod")
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

     /*@RequestMapping(path = {"/delete/{id}"}, method = RequestMethod.POST)
    public Employee deleteEmployeeById(@PathVariable("id") Integer id) {
        log.info("EmployeeServiceController -> deleteEmployeeById id [{}]", id);

        Employee employee = employees.get(id);
        if (employee != null) {
            employees.remove(id);
        }

        return employee;
    }*/


    /*private static final Map<Integer, Employee> employeeData = new HashMap<Integer, Employee>() {
        private static final long serialVersionUID = -3970206781360313502L;
        {
            put(111,new Employee("Employee1", 111));
            put(222,new Employee("Employee2", 222));
        }
    };

    @RequestMapping(value = "/findEmployeeDetails/{employeeId}", method = RequestMethod.GET)
    public Employee getEmployeeDetails(@PathVariable(name = "employeeId") int id) {
        log.info("Getting Employee Details For [{}]", id);

        Employee employee = employeeData.get(id);

        if (null == employee) {
            employee = new Employee("N/A", 0);
        }

        return employee;
    }*/

}
