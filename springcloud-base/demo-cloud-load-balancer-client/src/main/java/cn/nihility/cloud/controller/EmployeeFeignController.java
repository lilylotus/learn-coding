package cn.nihility.cloud.controller;

import cn.nihility.cloud.entity.Employee;
import cn.nihility.cloud.feign.EmployeeClient;
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
/*@DefaultProperties(defaultFallback = "defaultFallBack")*/
public class EmployeeFeignController {

    @Autowired
    private EmployeeClient employeeClient;

    /*@HystrixCommand(fallbackMethod = "fallbackMethod")*/
    @RequestMapping(path = {"/uri"}, method = RequestMethod.GET)
    public String serviceUri() {
        log.info("EmployeeFeignController -> serviceUri");

        String tag = employeeClient.getTag();
        log.info("tag [{}]", tag);

        return tag;
    }

    public String fallbackMethod() {
        log.info("EmployeeFeignController -> fallbackMethod");
        return "Fallback response:: No services details available temporarily";
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

    /*@HystrixCommand(fallbackMethod = "fallbackIdMethod",
            commandProperties = {@HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "3000")})*/
    /*@HystrixCommand(fallbackMethod = "fallbackIdMethod")*/
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Employee getEmployeeById(@PathVariable("id") Integer id) {
        log.info("EmployeeFeignController -> getEmployeeById id [{}]", id);

        final Employee employee = employeeClient.getEmployeeById(id);
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
        log.info("EmployeeFeignController -> addEmployeeById id [{}]", id);

        Employee employee = employeeClient.addEmployeeById(id);
        log.info("employee [{}]", employee);

        return employee;
    }

}
