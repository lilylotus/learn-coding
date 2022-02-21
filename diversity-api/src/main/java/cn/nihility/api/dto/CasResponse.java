package cn.nihility.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * @author nihility
 * @date 2022/02/21 17:45
 */
@Getter
@Setter
public class CasResponse {
    /**
     * 用户唯一标识
     */
    private String user;
    /**
     * 用户属性MAP
     */
    private Map<String, Object> attributes;

    @Override
    public String toString() {
        return "CasResponse{" +
            "user='" + user + '\'' +
            ", attributes=" + attributes +
            '}';
    }

}
