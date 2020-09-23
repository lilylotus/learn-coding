package cn.nihility.cloud.service;

import cn.nihility.cloud.domain.Employee;

import java.util.List;

public interface EmployeeService {

    Employee addEmployee(Employee employee);

    Employee deleteEmployeeById(Integer employeeId);

    Employee queryEmployeeById(Integer employeeId);

    List<Employee> queryEmployees();

}
