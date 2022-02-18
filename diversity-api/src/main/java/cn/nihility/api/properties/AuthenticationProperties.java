package cn.nihility.api.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author nihility
 * @date 2022/02/18 13:38
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "auth")
@Component
public class AuthenticationProperties {

    private String authLoginPrefix = "/auth/login";

    /**
     * 默认会话过期时间（秒）
     */
    private long inactiveInterval = 7200;

}
