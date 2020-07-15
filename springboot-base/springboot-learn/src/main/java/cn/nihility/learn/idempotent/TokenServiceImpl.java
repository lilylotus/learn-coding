package cn.nihility.learn.idempotent;

import cn.nihility.learn.common.Constant;
import cn.nihility.learn.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

/**
 * TokenServiceImpl
 *
 * @author dandelion
 * @date 2020-05-07 15:45
 */
@Service
public class TokenServiceImpl implements Idempotent {

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public String createToken() {
        try {
            String token = UUID.randomUUID().toString();
//            String token = Constant.Redis.TOKEN_PREFIX + rand;
            if (token.length() > 0) {
                redisUtil.setCacheEx(token, token, 10000L);
                return token;
            } else {
                return null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean checkToken(HttpServletRequest request) throws Exception {
        String token = request.getHeader(Constant.Idempotent.TOKEN_NAME);
        // header 当中不存在
        if (StringUtils.isBlank(token)) {
            token = request.getParameter(Constant.Idempotent.TOKEN_NAME);
            // parameter 中也为 空
            if (StringUtils.isBlank(token)) {
                throw new ServiceException("100", "参数错误");
            }
        }

        if (!redisUtil.exists(token)) {
            throw new ServiceException("200", "缓存中不存在 token");
        }

        /* 最后要删除缓存并校验是否删除成功 */
        final boolean remove = redisUtil.remove(token);
        if (!remove) {
            throw new ServiceException("200", "删除缓存 token 失败");
        }

        return true;
    }
}
