package cn.nihility.cloud.sentinel.feign;

import cn.nihility.cloud.sentinel.entity.Employee;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * EmployeeClient
 *
 * @author dandelion
 * @date 2020-04-24 16:28
 */
@FeignClient(name = "spring-cloud-service-provider", fallback = EmployeeClientFallBack.class)
public interface EmployeeClient {

    @RequestMapping(value = "/employee/tag", method = RequestMethod.GET)
    String getTag();

    @RequestMapping(value = "/employee/list", method = RequestMethod.GET)
    List<Employee> getEmployees();

    @RequestMapping(value = "/employee/{id}", method = RequestMethod.GET)
    Employee getEmployeeById(@PathVariable("id") Integer id);

    @RequestMapping(path = {"/employee/add/{id}"}, method = RequestMethod.POST)
    Employee addEmployeeById(@PathVariable("id") Integer id);

}
