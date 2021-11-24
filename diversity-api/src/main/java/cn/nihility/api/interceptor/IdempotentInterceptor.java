package cn.nihility.api.interceptor;

import cn.nihility.api.annotation.ApiIdempotent;
import cn.nihility.api.constant.Constant;
import cn.nihility.api.exception.IdempotentException;
import cn.nihility.api.service.IdempotentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 请求接口幂等性控制拦截器
 */
public final class IdempotentInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(IdempotentInterceptor.class);

    private IdempotentService idempotentService;

    public IdempotentInterceptor(IdempotentService idempotentService) {
        this.idempotentService = idempotentService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        /* 处理拦截方法 */
        if (!(handler instanceof HandlerMethod)) {
            return false;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        ApiIdempotent apiIdempotent = handlerMethod.getMethod().getAnnotation(ApiIdempotent.class);
        if (null != apiIdempotent && apiIdempotent.value()) {
            idempotentService.verify(acquireIdempotentKey(request));
        }

        return true;

    }

    /**
     * 获取请求中的 token key
     *
     * @param request 请求体
     * @return token key
     */
    private String acquireIdempotentKey(HttpServletRequest request) {
        String key;
        /*
         * 1. 现在 header 中获取 key
         * 2. 在 params 中获取 key
         */
        if (null == (key = request.getHeader(Constant.IDEMPOTENT_TOKEN_KEY))) {
            key = request.getParameter(Constant.IDEMPOTENT_TOKEN_KEY);
        }
        /* 没有携带幂等性处理 token key，抛出异常 */
        if (null == key) {
            throw new IdempotentException("幂等接口携带 token 不存在");
        }
        if (log.isDebugEnabled()) {
            log.debug("幂等校验 token [{}]", key);
        }
        return key;
    }

}
