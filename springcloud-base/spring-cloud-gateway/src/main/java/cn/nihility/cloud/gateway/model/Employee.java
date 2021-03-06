package cn.nihility.cloud.gateway.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Employee
 *
 * @author dandelion
 * @date 2020-04-12 23:20
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Employee {

    private Integer id;
    private String name;
    private String address;
    private String dept;

    public static Employee build(Integer id, String prefix) {
        return new Employee(id, prefix + ":build name",
                prefix + ":build address", prefix + ":build dept");
    }

    public static Employee build(Integer id) {
        return build(id, Integer.toString(id));
    }

}