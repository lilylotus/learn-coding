package cn.nihility.boot.actuator.springbootactuator2_x;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

/**
 * SimpleRestController
 *
 * @author dandelion
 * @date 2020-04-09 22:05
 */
@RestController
public class SimpleRestController {

    @GetMapping("/example")
    public String example() {
        return "Hello User !! " + LocalDateTime.now();
    }

}
