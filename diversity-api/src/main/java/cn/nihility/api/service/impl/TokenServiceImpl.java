package cn.nihility.api.service.impl;

import cn.nihility.api.properties.AuthenticationProperties;
import cn.nihility.api.service.ITokenService;
import cn.nihility.common.entity.AuthenticationToken;
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
    private final AuthenticationProperties authenticationProperties;

    public TokenServiceImpl(RedissonOperateService redissonOperate,
                            AuthenticationProperties authenticationProperties) {
        this.redissonOperate = redissonOperate;
        this.authenticationProperties = authenticationProperties;
    }

    private void createToken(AuthenticationToken token, String tokenKey) {
        token.setTtl(authenticationProperties.getInactiveInterval());
        RBucket<AuthenticationToken> bucket = redissonOperate.getBucket(tokenKey);
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

}
