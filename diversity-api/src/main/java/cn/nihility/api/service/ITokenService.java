package cn.nihility.api.service;

import cn.nihility.common.constant.OidcConstant;
import cn.nihility.common.entity.AuthenticationToken;
import cn.nihility.common.util.JwtUtils;

/**
 * @author nihility
 * @date 2022/02/18 17:51
 */
public interface ITokenService {

    String AUTH_PREFIX = "auth-token";

    String OAUTH_PREFIX = "oauth-token";

    String CAS_PREFIX = "cas-token";

    String JWT_PREFIX = "auth-jwt";

    default String tokenKey(String prefix, String id) {
        return prefix + ":" + id;
    }

    default String casKey(String id) {
        return CAS_PREFIX + ":" + id;
    }

    default String oauthKey(String id) {
        return OAUTH_PREFIX + ":" + id;
    }

    default String oidcKey(String id) {
        return OidcConstant.OIDC_TOKEN_PREFIX + ":" + id;
    }

    default String jwtKey(String id) {
        return JWT_PREFIX + ":" + id;
    }

    void createOauthToken(AuthenticationToken accessToken);

    AuthenticationToken getOauthToken(String id);

    boolean deleteOauthToken(String code);

    void createCasToken(AuthenticationToken accessToken);

    AuthenticationToken getCasToken(String id);

    boolean deleteCasToken(String code);

    void createOidcToken(AuthenticationToken token);

    AuthenticationToken getOidcToken(String key);

    boolean deleteOidcToken(String key);

    void createJwt(String id, JwtUtils.JwtHolder jwt);

}
