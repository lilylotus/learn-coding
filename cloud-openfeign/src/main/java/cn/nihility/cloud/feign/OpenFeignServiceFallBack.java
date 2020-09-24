package cn.nihility.cloud.feign;

import cn.nihility.cloud.domain.Employee;
import cn.nihility.cloud.domain.ResultResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class OpenFeignServiceFallBack implements OpenFeignService {

    private static final Logger log = LoggerFactory.getLogger(OpenFeignServiceFallBack.class);
    private static final Employee EMPTY_EMPLOYEE = new Employee(0, "empty", "empty");

    @Override
    public Map<String, Object> commonId() {
        log.error("commonId (Feign Hystrix 熔断降级)");
        Map<String, Object> ret = new HashMap<>(4);
        ret.put("msg", "(Feign Hystrix 熔断降级)");
        ret.put("point", "commonId");
        return ret;
    }

    @Override
    public ResultResponse<List<Employee>> getEmployees() {
        log.error("getEmployees (Feign Hystrix 熔断降级)");
        List<Employee> list = new ArrayList<>();
        return ResultResponse.failure(list).withMessage("(Feign Hystrix 熔断降级)");
    }

    @Override
    public ResultResponse<Employee> getEmployeeById(Integer id) {
        log.error("getEmployeeById (Feign Hystrix 熔断降级)");
        return ResultResponse.failure(EMPTY_EMPLOYEE).withMessage("(Feign Hystrix 熔断降级)");
    }

    @Override
    public ResultResponse<Employee> addEmployeeById(Integer id) {
        log.error("addEmployeeById (Feign Hystrix 熔断降级)");
        return ResultResponse.failure(EMPTY_EMPLOYEE).withMessage("(Feign Hystrix 熔断降级)");
    }

    @Override
    public ResultResponse<Employee> deleteEmployeeById(Integer id) {
        log.error("deleteEmployeeById (Feign Hystrix 熔断降级)");
        return ResultResponse.failure(EMPTY_EMPLOYEE).withMessage("(Feign Hystrix 熔断降级)");
    }

    @Override
    public ResultResponse<Employee> addEmployee(Employee employee) {
        log.error("addEmployee (Feign Hystrix 熔断降级)");
        return ResultResponse.failure(EMPTY_EMPLOYEE).withMessage("(Feign Hystrix 熔断降级)");
    }
}
