package cn.nihility.common.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Map;

/**
 * @author nihility
 * @date 2022/02/18 14:03
 */
@Getter
@Setter
public class AuthenticateSession implements Serializable {

    private static final long serialVersionUID = -7918563683646004169L;

    private String sessionId;

    private Map<String, Object> authResult;

    private String userId;

    private Map<String, Object> userAttributes;

    private Long createTime;

    private Long updateTime;

    private Long ttl;

}
