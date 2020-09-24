package cn.nihility.cloud.controller;

import cn.nihility.cloud.domain.Employee;
import cn.nihility.cloud.domain.ResultResponse;
import cn.nihility.cloud.feign.OpenFeignService;
import cn.nihility.cloud.hystrix.ServiceHystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/feign")
public class OpenFeignController {

    private static final Logger log = LoggerFactory.getLogger(OpenFeignController.class);
    private final OpenFeignService openFeignService;
    private final String mark;
    private final RestTemplate restTemplate;

    public OpenFeignController(OpenFeignService openFeignService,
                               @Value("${server.tag}") String serviceTag,
                               @Value("${server.port}") Integer localPort,
                               RestTemplate restTemplate) {
        this.openFeignService = openFeignService;
        this.restTemplate = restTemplate;
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

    @RequestMapping(value = "/common/id", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> commonId() {
        Map<String, Object> map = openFeignService.commonId();
        log.info("result info [{}]", map);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @RequestMapping(value = "/employee/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultResponse<Employee> getEmployeeById(@PathVariable() Integer id) {
        log.info("getEmployeeById id [{}]", id);
        ResultResponse<Employee> info = openFeignService.getEmployeeById(id);
        log.info("result info [{}]", info);
        return info;
    }

    @RequestMapping(path = {"/employee/{id}"}, method = RequestMethod.POST)
    public ResultResponse<Employee> addEmployeeById(@PathVariable("id") Integer id) {
        log.info("addEmployeeById id [{}]", id);
        ResultResponse<Employee> response = openFeignService.addEmployeeById(id);
        log.info("response info [{}]", response);
        return response;
    }

    @RequestMapping(path = {"/employee/delete/{id}"}, method = RequestMethod.POST)
    public ResultResponse<Employee> deleteEmployeeById(@PathVariable("id") Integer id) {
        log.info("deleteEmployeeById [{}]", id);
        ResultResponse<Employee> response = openFeignService.deleteEmployeeById(id);
        log.info("response info [{}]", response);
        return response;
    }

    @RequestMapping(value = "/employees")
    public ResultResponse<List<Employee>> getEmployees() {
        log.info("get employees");
        return openFeignService.getEmployees();
    }

    @RequestMapping(value = "/employee/hystrix/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultResponse<Employee> getEmployeeByHystrixId(@PathVariable() Integer id) {
        log.info("getEmployeeById hystrix command id [{}]", id);
        ServiceHystrixCommand command = new ServiceHystrixCommand(restTemplate, id);
        ResultResponse info = command.execute();
        log.info("result info [{}]", info);
        return info;
    }

    @HystrixCommand(fallbackMethod = "fallbackMethod",
        commandProperties = {@HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "2000")})
    @RequestMapping(value = "/employee/hystrix/manual/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultResponse<Employee> getEmployeeByHystrixManualId(@PathVariable() Integer id) {
        log.info("getEmployeeById hystrix command id [{}]", id);
        ResultResponse info = restTemplate.getForObject("http://localhost:41100/employee/{id}", ResultResponse.class, id);
        log.info("result info [{}]", info);
        return info;
    }

    /**
     *  Hystrix 降级的方法和需要保护的方法返回值类型一致
     */
    private ResultResponse<Employee> fallbackMethod(Integer id) {
        log.info("Invoke Hystrix Command Annotation fallbackMethod Id [{}]", id);
        return ResultResponse.failure(new Employee(id, "default hystrix fallbackMethod", "hystrix"))
                .withMessage("(Feign Hystrix 熔断降级)");
    }

}
