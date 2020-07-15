package cn.nihility.boot.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 普通的 bean 依赖注入测试
 * 基础的 Annotation Bean 注入，注解支持 Component/Service/Controller/Repository
 * 注意：
 *
 * ConfigurationProperties(prefix = "bean")
 *
 * @author clover
 * @date 2020-01-08 13:06
 */
@Component
@Getter
@Setter
@ToString
@ConfigurationProperties(prefix = "inject")
public class InjectBeanDemo {

    private String type = "Injection Bean Demo";
    private String name;
    private Integer age;
    private List<String> address;
    private List<String> address1;
    private Map<String, Integer> grades; // 成绩，科目：分数
    private Map<String, Integer> grades1; // 成绩，科目：分数
    private Map<String, List<String>> group;

}
