package cn.nihility.common.util;

import cn.nihility.common.constant.Oauth2Constant;
import cn.nihility.common.entity.AuthenticationToken;

/**
 * @author nihility
 * @date 2022/02/18 17:46
 */
public class AuthenticationUtils {

    private AuthenticationUtils() {
    }

    public static AuthenticationToken createToken(String sessionId, String protocol,
                                                  String type, String tokenId) {
        AuthenticationToken token = new AuthenticationToken();
        token.setSessionId(sessionId);
        token.setProtocol(protocol);
        token.setType(type);
        token.setTokenId(tokenId);
        token.setTtl(Oauth2Constant.TOKEN_TTL);
        token.setCreateTime(System.currentTimeMillis());
        token.setUpdateTime(System.currentTimeMillis());
        return token;
    }

    public static String tokenId(String prefix) {
        return prefix + "-" + UuidUtils.jdkUUID();
    }

}
