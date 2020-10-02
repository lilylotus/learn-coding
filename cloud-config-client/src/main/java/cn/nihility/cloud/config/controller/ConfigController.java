package cn.nihility.cloud.config.controller;

import cn.nihility.cloud.config.component.User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author daffodil
 * @date 2020-10-02 23:52:56
 */
@RestController
@RequestMapping("/config")
public class ConfigController {

    private final User user;

    public ConfigController(User user) {
        this.user = user;
    }

    @RequestMapping("/usr")
    public User getUser() {
        return user;
    }

}
