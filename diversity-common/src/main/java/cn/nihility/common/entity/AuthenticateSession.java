package cn.nihility.common.entity;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author nihility
 * @date 2022/02/18 14:03
 */
@Getter
@Setter
public class AuthenticateSession implements Serializable {

    private static final long serialVersionUID = -7918563683646004169L;

    private String sessionId;

    private Set<AuthenticationToken> tokenSet;

    private Map<String, Object> authResult;

    private String userId;

    private Map<String, Object> userAttributes;

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

}
