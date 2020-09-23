package cn.nihility.cloud.service;

import cn.nihility.cloud.domain.Employee;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    //private static final Logger log = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    private static final Map<Integer, Employee> EMPLOYEES_MAP = new ConcurrentHashMap<>();

    static {
        for (int i = 0; i < 10; i++) {
            EMPLOYEES_MAP.put(i, Employee.build(i));
        }
    }

    @Override
    public Employee addEmployee(Employee employee) {
        EMPLOYEES_MAP.put(employee.getId(), employee);
        return employee;
    }

    @Override
    public Employee deleteEmployeeById(Integer employeeId) {
        return EMPLOYEES_MAP.remove(employeeId);
    }

    @Override
    public Employee queryEmployeeById(Integer employeeId) {
        return EMPLOYEES_MAP.get(employeeId);
    }

    @Override
    public List<Employee> queryEmployees() {
        return new ArrayList<>(EMPLOYEES_MAP.values());
    }

}
