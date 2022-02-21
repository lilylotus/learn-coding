package cn.nihility.common.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author nihility
 * @date 2022/02/18 17:43
 */
@Setter
@Getter
@ToString
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

}
