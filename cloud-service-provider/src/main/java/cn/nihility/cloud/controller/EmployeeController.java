package cn.nihility.cloud.controller;

import cn.nihility.cloud.domain.Employee;
import cn.nihility.cloud.domain.ResultResponse;
import cn.nihility.cloud.service.EmployeeService;
import org.apache.commons.lang.math.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employee")
public class EmployeeController {

    private static final Logger log = LoggerFactory.getLogger(EmployeeController.class);

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @RequestMapping(value = "/employees")
    public ResultResponse<List<Employee>> getEmployees() {
        log.info("get employees");
        return ResultResponse.success(employeeService.queryEmployees());
    }


    @RequestMapping(value = "/{id}")
    public ResultResponse<Employee> getEmployeeById(@PathVariable("id") Integer id) {
        log.info("getEmployeeById [{}]", id);

        /* 测试高并发，模拟数据请求 */
        try {
            int delay = RandomUtils.nextInt(10) + 1;
            log.info("delay time [{}]", delay);
            Thread.sleep(delay * 1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return ResultResponse.success(employeeService.queryEmployeeById(id));
    }

    @RequestMapping(path = {"/{id}"}, method = RequestMethod.POST)
    public ResultResponse<Employee> addEmployeeById(@PathVariable("id") Integer id) {
        log.info("addEmployeeById id [{}]", id);
        return ResultResponse.success(employeeService.addEmployee(Employee.build(id)));
    }

    @RequestMapping(path = {"/delete/{id}"}, method = RequestMethod.POST)
    public ResultResponse<Employee> deleteEmployeeById(@PathVariable("id") Integer id) {
        log.info("deleteEmployeeById [{}]", id);
        return ResultResponse.success(employeeService.deleteEmployeeById(id));
    }

    @RequestMapping(path = {"/add"}, method = RequestMethod.POST)
    public ResultResponse<Employee> addEmployee(@RequestBody Employee employee) {
        log.info("addEmployee [{}]", employee);
        return ResultResponse.success(employeeService.addEmployee(employee));
    }


}
