package cn.nihility.api.service;

import cn.nihility.common.entity.AuthenticationToken;

/**
 * @author nihility
 * @date 2022/02/18 17:51
 */
public interface ITokenService {

    String AUTH_PREFIX = "auth-token";

    String OAUTH_PREFIX = "oauth-token";

    String CAS_PREFIX = "cas-token";

    default String casKey(String id) {
        return CAS_PREFIX + ":" + id;
    }

    default String oauthKey(String id) {
        return OAUTH_PREFIX + ":" + id;
    }

    default String authKey(String id) {
        return AUTH_PREFIX + ":" + id;
    }

    void createOauthToken(AuthenticationToken accessToken);

    AuthenticationToken getOauthToken(String id);

    boolean deleteOauthToken(String code);

    void createCasToken(AuthenticationToken accessToken);

    AuthenticationToken getCasToken(String id);

    boolean deleteCasToken(String code);

}
