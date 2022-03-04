package cn.nihility.api.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author nihility
 * @date 2022/02/18 13:38
 */
@ConfigurationProperties(prefix = "auth")
@Component
public class AuthenticationProperties {

    private String authLoginPrefix = "/auth/login";

    /**
     * 默认会话过期时间（秒）
     */
    private long inactiveInterval = 7200;

    private String ssoHost;

    /* ============================== getter/setter ============================== */

    public String getAuthLoginPrefix() {
        return authLoginPrefix;
    }

    public void setAuthLoginPrefix(String authLoginPrefix) {
        this.authLoginPrefix = authLoginPrefix;
    }

    public long getInactiveInterval() {
        return inactiveInterval;
    }

    public void setInactiveInterval(long inactiveInterval) {
        this.inactiveInterval = inactiveInterval;
    }

    public String getSsoHost() {
        return ssoHost;
    }

    public void setSsoHost(String ssoHost) {
        this.ssoHost = ssoHost;
    }
    
}
