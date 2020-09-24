package cn.nihility.cloud.feign;

import cn.nihility.cloud.domain.Employee;
import cn.nihility.cloud.domain.ResultResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.Map;

/**
 * ${@link FeignClient} 指定要调用微服务的服务注册名
 */
@FeignClient(value = "cloud-service-provider", fallback = OpenFeignServiceFallBack.class)
public interface OpenFeignService {

    @RequestMapping("/common/id")
    Map<String, Object> commonId();

    @RequestMapping(value = "/employee/employees")
    ResultResponse<List<Employee>> getEmployees();

    @RequestMapping(value = "/employee/{id}")
    ResultResponse<Employee> getEmployeeById(@PathVariable("id") Integer id);

    @RequestMapping(path = {"/employee/{id}"}, method = RequestMethod.POST)
    ResultResponse<Employee> addEmployeeById(@PathVariable("id") Integer id);

    @RequestMapping(path = {"/employee/delete/{id}"}, method = RequestMethod.POST)
    ResultResponse<Employee> deleteEmployeeById(@PathVariable("id") Integer id);

    @RequestMapping(path = {"/employee/add"}, method = RequestMethod.POST)
    ResultResponse<Employee> addEmployee(@RequestBody Employee employee);

}
