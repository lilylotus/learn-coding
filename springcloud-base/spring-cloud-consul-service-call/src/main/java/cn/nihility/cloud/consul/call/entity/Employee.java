package cn.nihility.cloud.consul.call.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Employee
 *
 * @author dandelion
 * @date 2020-04-24 14:06
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Employee {

    private Integer id;
    private String name;
    private String address;
    private String dept;
    private String tag;

    public static Employee build(Integer id, String prefix, String tag) {
        return new Employee(id, prefix + ":build name",
                prefix + ":build address", prefix + ":build dept", tag);
    }

    public static Employee build(Integer id, String prefix) {
        return new Employee(id, prefix + ":build name",
                prefix + ":build address", prefix + ":build dept", null);
    }

    public static Employee build(Integer id) {
        return build(id, Integer.toString(id));
    }

}
