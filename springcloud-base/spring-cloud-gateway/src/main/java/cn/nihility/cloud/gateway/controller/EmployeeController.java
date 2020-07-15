package cn.nihility.cloud.gateway.controller;

import cn.nihility.cloud.gateway.model.Employee;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * EmployeeController
 *
 * @author dandelion
 * @date 2020-04-12 23:50
 */
@Slf4j
@RestController
@RequestMapping("/emp")
public class EmployeeController {

    private RestTemplate restTemplate;

    public EmployeeController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @RequestMapping(value = {"/findEmployeeDetails/{employeeId}"}, method = RequestMethod.GET)
    @HystrixCommand(fallbackMethod = "fallbackMethod")
    public String getEmployees(@PathVariable(name = "employeeId") int employeeId) {
        System.out.println("Getting Employee Details For " + employeeId);
        log.info("Getting Employee Details For [{}]", employeeId);

        String remoteUrl = "http://spring-cloud-eureka-client/employee/findEmployeeDetails/{employeeId}";

        final ResponseEntity<String> request = restTemplate.exchange(remoteUrl,
                HttpMethod.GET, null,
                new ParameterizedTypeReference<String>() {
                },
                employeeId);
        final String response = request.getBody();

        /*final ResponseEntity<Employee> request = restTemplate
                .getForEntity(remoteUrl,
                        Employee.class, employeeId);
        final Employee response = request.getBody();*/

        log.info("response [{}]", response);
        System.out.println("response " + response);

        return "Employee Id - " + employeeId + " [ " + response + " ]";
    }

    public String fallbackMethod(int employeeId) {
        log.info("fallbackMethod employee id [{}]", employeeId);
        return "Fallback response:: No employee details available temporarily";
    }

}
