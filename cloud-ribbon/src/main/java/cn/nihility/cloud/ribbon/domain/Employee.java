package cn.nihility.cloud.ribbon.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Employee {

    private Integer id;
    private String name;
    private String address;

    public static Employee build(Integer id) {
        return new Employee(id,
                "build:" + UUID.randomUUID().toString().substring(0, 6),
                "build:" + UUID.randomUUID().toString());
    }

}
