package cn.nihility.cloud.service.controller;

import cn.nihility.cloud.service.entity.Employee;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * EmployeeServiceController
 *
 * @author dandelion
 * @date 2020-04-24 14:03
 */
@RestController
@RequestMapping("/employee")
@Slf4j
public class EmployeeServiceController {

    private static final Map<Integer, Employee> employees = new HashMap<>();

    @Autowired
    private Environment environment;

    static {
        employees.put(0, Employee.build(0, "default"));
        employees.put(100, Employee.build(100));
        employees.put(200, Employee.build(200));
        employees.put(300, Employee.build(300));
        employees.put(400, Employee.build(400));
        employees.put(500, Employee.build(500));
    }

    @RequestMapping(value = "/tag", method = RequestMethod.GET)
    public String getTag() {
        String tag = environment.getProperty("local.server.port");
        log.info("EmployeeServiceController -> getTag tag [{}]", tag);
        return tag;
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public List<Employee> getEmployees() {
        log.info("EmployeeServiceController -> getEmployees");
        return new ArrayList<>(employees.values());
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Employee getEmployeeById(@PathVariable("id") Integer id) {
        log.info("EmployeeServiceController -> getEmployeeById id [{}]", id);

        /* 测试高并发，模拟数据请求 */
        /*try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

        return employees.get(id);
    }

    @RequestMapping(path = {"/add/{id}"}, method = RequestMethod.POST)
    public Employee addEmployeeById(@PathVariable("id") Integer id) {
        log.info("EmployeeServiceController -> addEmployeeById id [{}]", id);

        Employee employee = Employee.build(id);
        employees.put(id, employee);

        return employee;
    }

    @RequestMapping(path = {"/delete/{id}"}, method = RequestMethod.POST)
    public Employee deleteEmployeeById(@PathVariable("id") Integer id) {
        log.info("EmployeeServiceController -> deleteEmployeeById id [{}]", id);

        Employee employee = employees.get(id);
        if (employee != null) {
            employees.remove(id);
        }

        return employee;
    }

}
