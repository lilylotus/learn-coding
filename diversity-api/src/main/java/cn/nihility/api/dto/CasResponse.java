package cn.nihility.api.dto;

import java.util.Map;

/**
 * @author nihility
 * @date 2022/02/21 17:45
 */
public class CasResponse {
    /**
     * 用户唯一标识
     */
    private String user;
    /**
     * 用户属性MAP
     */
    private Map<String, Object> attributes;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String toString() {
        return "CasResponse{" +
            "user='" + user + '\'' +
            ", attributes=" + attributes +
            '}';
    }

}
