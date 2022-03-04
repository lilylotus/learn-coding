package cn.nihility.common.entity;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author nihility
 * @date 2022/02/18 17:43
 */
public class AuthenticationToken implements Serializable {

    private static final long serialVersionUID = 3640784304492446272L;

    private String sessionId;

    private String tokenId;

    private String refTokenId;

    private String protocol;

    private String type;

    private long createTime;

    private long updateTime;

    private long ttl;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AuthenticationToken that = (AuthenticationToken) o;
        return sessionId.equals(that.sessionId) &&
            tokenId.equals(that.tokenId) &&
            protocol.equals(that.protocol) &&
            type.equals(that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sessionId, tokenId, protocol, type);
    }

    /* ============================== getter/setter ============================== */

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public String getRefTokenId() {
        return refTokenId;
    }

    public void setRefTokenId(String refTokenId) {
        this.refTokenId = refTokenId;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public long getTtl() {
        return ttl;
    }

    public void setTtl(long ttl) {
        this.ttl = ttl;
    }
}
