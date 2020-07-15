package cn.nihility.learn.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * AutoImportBean
 *
 * @author dandelion
 * @date 2020-03-24 16:48
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties(prefix = "learn.auto")
public class AutoImportBean {

    private String name;
    private Integer age;
    private String address;

}
