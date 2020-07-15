package cn.nihility.cloud.feign;

import cn.nihility.cloud.entity.Employee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * EmployeeClientFallBack
 *
 * @author dandelion
 * @date 2020-04-26 15:50
 */
@Component
public class EmployeeClientFallBack implements EmployeeClient {

    private static final Employee EMPLOYEE =
            new Employee(0, "Feign Hystrix 熔断降级", "Feign Hystrix 熔断降级", "Feign Hystrix 熔断降级");
    private static final Logger log = LoggerFactory.getLogger(EmployeeClientFallBack.class);

    @Override
    public String getTag() {
        log.info("EmployeeClientFallBack -> getTag (Feign Hystrix 熔断降级)");
        return "Feign Hystrix 熔断降级";
    }

    @Override
    public List<Employee> getEmployees() {
        log.info("EmployeeClientFallBack -> getEmployees (Feign Hystrix 熔断降级)");
        List<Employee> list = new ArrayList<>();
        list.add(EMPLOYEE);
        return list;
    }

    @Override
    public Employee getEmployeeById(Integer id) {
        log.info("EmployeeClientFallBack -> getEmployeeById (Feign Hystrix 熔断降级) id [{}]", id);
        return EMPLOYEE;
    }

    @Override
    public Employee addEmployeeById(Integer id) {
        log.info("EmployeeClientFallBack -> addEmployeeById (Feign Hystrix 熔断降级) id [{}]", id);
        return EMPLOYEE;
    }
}
