package cn.nihility.common.entity;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author nihility
 * @date 2022/02/18 14:03
 */
public class AuthenticateSession implements Serializable {

    private static final long serialVersionUID = -7918563683646004169L;

    private String sessionId;

    private transient Set<AuthenticationToken> tokenSet;

    private Map<String, Object> authResult;

    private String userId;

    private transient Map<String, Object> userAttributes;

    private Long createTime;

    private Long updateTime;

    private Long ttl;

    public void addToken(AuthenticationToken token) {
        if (null == tokenSet) {
            tokenSet = new HashSet<>(4);
        }
        tokenSet.add(token);
    }

    public AuthenticationToken queryToken(String tokenId) {
        if (null == tokenSet || StringUtils.isBlank(tokenId)) {
            return null;
        }
        return tokenSet.stream().filter(t -> t.getTokenId().equals(tokenId))
            .findFirst().orElse(null);
    }

    /* ============================== getter/setter ============================== */

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Set<AuthenticationToken> getTokenSet() {
        return tokenSet;
    }

    public void setTokenSet(Set<AuthenticationToken> tokenSet) {
        this.tokenSet = tokenSet;
    }

    public Map<String, Object> getAuthResult() {
        return authResult;
    }

    public void setAuthResult(Map<String, Object> authResult) {
        this.authResult = authResult;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Map<String, Object> getUserAttributes() {
        return userAttributes;
    }

    public void setUserAttributes(Map<String, Object> userAttributes) {
        this.userAttributes = userAttributes;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    public Long getTtl() {
        return ttl;
    }

    public void setTtl(Long ttl) {
        this.ttl = ttl;
    }
}
