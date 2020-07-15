package cn.nihility.boot.service;

import cn.nihility.boot.exception.AuthenticationException;
import cn.nihility.boot.util.RedisTemplateUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

/**
 * @author dandelion
 * @date 2020:06:27 20:06
 */
@Service
public class TokenServiceImpl implements IdempotentCheck, IdempotentCreate {

    private final static Logger log = LoggerFactory.getLogger(TokenServiceImpl.class);
    private final static String TOKEN_KEY = "token";

    private RedisTemplateUtil redisTemplateUtil;

    public TokenServiceImpl(RedisTemplateUtil redisTemplateUtil) {
        this.redisTemplateUtil = redisTemplateUtil;
    }

    @Override
    public boolean checkToken(HttpServletRequest request) {
        String token = request.getHeader(TOKEN_KEY);
        // header 不存在
        if (StringUtils.isBlank(token)) {
            token = request.getParameter(TOKEN_KEY);
            if (StringUtils.isBlank(token)) {
                throw new AuthenticationException("401, 认证参数不存在");
            }
        }

        log.info("check token [{}], session [{}]", token, request.getSession().getId());

        if (!redisTemplateUtil.exists(token)) {
            throw new AuthenticationException("401, 缓存 token 失效");
        }

        boolean remove = redisTemplateUtil.remove(token);
        if (!remove) {
            throw new AuthenticationException("401, 删除 token 缓存失效");
        }

        return true;
    }

    @Override
    public String createToken() {
        String token = UUID.randomUUID().toString().replace("-", "");
        return redisTemplateUtil.setCacheEx(token, token, 1200L) ? token : null;
    }

}
