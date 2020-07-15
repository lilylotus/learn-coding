package cn.nihility.boot.actuator.springbootactuator1;

import org.springframework.boot.actuate.endpoint.Endpoint;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * CustomEndpoint
 *
 * @author dandelion
 * @date 2020-04-09 22:39
 */
@Component
public class CustomEndpoint implements Endpoint<List<String>> {


    @Override
    public String getId() {
        return "customEndpoint";
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean isSensitive() {
        return true;
    }

    @Override
    public List<String> invoke() {
        List<String> list = new ArrayList<>();

        list.add("This is Custom Actuator Endpoint 1");
        list.add("This is Custom Actuator Endpoint 2");
        list.add("This is Custom Actuator Endpoint 3");

        return list;
    }
}
