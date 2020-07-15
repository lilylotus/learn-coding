package cn.nihility.jdk8;

import java.util.List;

/**
 * Company
 *
 * @author clover
 * @date 2020-01-04 15:46
 */
public class Company {

    private String name;
    private List<FunctionPerson> employee;

    public Company(String name) {
        this.name = name;
    }

    public Company() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<FunctionPerson> getEmployee() {
        return employee;
    }

    public void setEmployee(List<FunctionPerson> employee) {
        this.employee = employee;
    }

    @Override
    public String toString() {
        return "Company{" +
                "name='" + name + '\'' +
                ", employee=" + employee +
                '}';
    }
}
