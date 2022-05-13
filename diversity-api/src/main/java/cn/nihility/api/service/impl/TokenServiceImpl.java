package cn.nihility.api.service.impl;

import cn.nihility.api.service.ITokenService;
import cn.nihility.common.entity.AuthenticationToken;
import cn.nihility.common.util.JwtUtils;
import cn.nihility.plugin.redis.service.RedissonOperateService;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RBucket;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @author nihility
 * @date 2022/02/18 17:58
 */
@Service
public class TokenServiceImpl implements ITokenService {

    private final RedissonOperateService redissonOperate;

    public TokenServiceImpl(RedissonOperateService redissonOperate) {
        this.redissonOperate = redissonOperate;
    }

    private void createToken(AuthenticationToken token, String key) {
        RBucket<AuthenticationToken> bucket = redissonOperate.getBucket(key);
        bucket.set(token, token.getTtl(), TimeUnit.SECONDS);
    }

    public AuthenticationToken getToken(String key) {
        RBucket<AuthenticationToken> bucket = redissonOperate.getBucket(key);
        return bucket.get();
    }

    /* ========== CAS ========== */

    @Override
    public void createCasToken(AuthenticationToken accessToken) {
        createToken(accessToken, casKey(accessToken.getTokenId()));
    }

    @Override
    public AuthenticationToken getCasToken(String id) {
        return StringUtils.isBlank(id) ? null : getToken(casKey(id));
    }

    @Override
    public boolean deleteCasToken(String code) {
        return redissonOperate.getBucket(casKey(code)).delete();
    }

    /* ========== oauth2 ========== */

    @Override
    public void createOauthToken(AuthenticationToken accessToken) {
        createToken(accessToken, oauthKey(accessToken.getTokenId()));
    }

    @Override
    public AuthenticationToken getOauthToken(String id) {
        return StringUtils.isBlank(id) ? null : getToken(oauthKey(id));
    }

    @Override
    public boolean deleteOauthToken(String code) {
        return redissonOperate.getBucket(oauthKey(code)).delete();
    }

    /* ========== OIDC ========== */

    @Override
    public void createOidcToken(AuthenticationToken token) {
        createToken(token, oidcKey(token.getTokenId()));
    }

    @Override
    public AuthenticationToken getOidcToken(String key) {
        return StringUtils.isBlank(key) ? null : getToken(oidcKey(key));
    }

    @Override
    public boolean deleteOidcToken(String key) {
        return redissonOperate.getBucket(oidcKey(key)).delete();
    }

    @Override
    public void createJwt(String id, JwtUtils.JwtHolder jwt) {
        RBucket<String> bucket = redissonOperate.getBucket(jwtKey(id));
        bucket.set(jwt.getAccessToken(), jwt.getExpiresIn(), TimeUnit.SECONDS);
    }

}
