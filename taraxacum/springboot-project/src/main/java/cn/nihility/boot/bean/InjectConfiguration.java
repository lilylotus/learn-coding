package cn.nihility.boot.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dandelion
 * @date 2020:06:26 18:55
 */
@Component
@Getter
@Setter
@ToString
@ConfigurationProperties(prefix = "my")
public class InjectConfiguration {

    private List<String> servers = new ArrayList<>();
    private List<MyPojo> pojoList = new ArrayList<>();

    private String firstName;
    private String lastName;
    private String fullName;
    private String address;

    @Getter
    @Setter
    @ToString
    static class MyPojo {
        private String name;
        private String description;
    }

}
