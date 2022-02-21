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

    private RedissonOperateService redissonOperate;
    private AuthenticationProperties authenticationProperties;

    public TokenServiceImpl(RedissonOperateService redissonOperate,
                            AuthenticationProperties authenticationProperties) {
        this.redissonOperate = redissonOperate;
        this.authenticationProperties = authenticationProperties;
    }

    private String buildKey(String key) {
        return TOKEN_PREFIX + ":" + key;
    }

    @Override
    public void createToken(AuthenticationToken token) {
        String tokenKey = buildKey(token.getTokenId());
        token.setTtl(authenticationProperties.getInactiveInterval());
        RBucket<AuthenticationToken> bucket = redissonOperate.getBucket(tokenKey);
        bucket.set(token, token.getTtl(), TimeUnit.SECONDS);
    }

    @Override
    public AuthenticationToken getTokenById(String id) {
        if (StringUtils.isBlank(id)) {
            return null;
        }
        RBucket<AuthenticationToken> bucket = redissonOperate.getBucket(buildKey(id));
        return bucket.get();
    }

    @Override
    public boolean deleteToken(String id) {
        RBucket<Object> bucket = redissonOperate.getBucket(buildKey(id));
        return bucket.delete();
    }

}
