package cn.nihility.api.service;

import cn.nihility.common.entity.AuthenticationToken;

/**
 * @author nihility
 * @date 2022/02/18 17:51
 */
public interface ITokenService {

    String TOKEN_PREFIX = "auth-token";

    void createToken(AuthenticationToken accessToken);

    AuthenticationToken getTokenById(String id);

}
