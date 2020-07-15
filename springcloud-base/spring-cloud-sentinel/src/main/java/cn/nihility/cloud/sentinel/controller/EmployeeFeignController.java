package cn.nihility.cloud.sentinel.controller;

import cn.nihility.cloud.sentinel.entity.Employee;
import cn.nihility.cloud.sentinel.feign.EmployeeClient;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * EmployeeFeignController
 *
 * @author dandelion
 * @date 2020-04-24 16:32
 */
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeFeignController {

    @Autowired
    private EmployeeClient employeeClient;

    @RequestMapping(path = {"/uri"}, method = RequestMethod.GET)
    public String serviceUri() {
        log.info("EmployeeFeignController -> serviceUri");

        String tag = employeeClient.getTag();
        log.info("tag [{}]", tag);

        return tag;
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public List<Employee> getEmployees() {
        log.info("EmployeeFeignController -> getEmployees");

        List<Employee> employees = employeeClient.getEmployees();

        if (employees != null) {
            log.info("Employees [{}]", Stream.of(employees).map(Object::toString).reduce("|", String::concat));
            return employees;
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * @SentinelResource
     *      blockHandler: 熔断降级的调用方法
     *      fallback: 抛出异常的降级方法
     */
//    @SentinelResource(value = "getEmployeeById", blockHandler = "serviceBlockHandler", fallback = "serviceFallBack")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Employee getEmployeeById(@PathVariable("id") Integer id) {
        log.info("EmployeeFeignController -> getEmployeeById id [{}]", id);

        if (10 == id) {
            log.error("请求异常，触发熔断 id [{}]", id);
            throw new RuntimeException("请求异常，触发熔断");
        }

        final Employee employee = employeeClient.getEmployeeById(id);
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

    @RequestMapping(path = {"/add/{id}"}, method = RequestMethod.POST)
    public Employee addEmployeeById(@PathVariable("id") Integer id) {
        log.info("EmployeeFeignController -> addEmployeeById id [{}]", id);

        Employee employee = employeeClient.addEmployeeById(id);
        log.info("employee [{}]", employee);

        return employee;
    }

}
