package cn.nihility.unify.interceptor;

import cn.nihility.unify.annotaion.ApiIdempotent;
import cn.nihility.unify.constant.Constants;
import cn.nihility.unify.exception.IdempotentException;
import cn.nihility.unify.idempotent.IdempotentService;
import cn.nihility.unify.idempotent.impl.IdempotentServiceImpl;
import cn.nihility.unify.pojo.UnifyResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 幂等性实现拦截器
 *
 * @author daffodil
 * @date 2020-10-23 22:44:12
 */
public class IdempotentInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(IdempotentInterceptor.class);

    private final IdempotentService idempotentService;

    public IdempotentInterceptor(IdempotentService idempotentService) {
        this.idempotentService = idempotentService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        /* 处理拦截方法 */
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        ApiIdempotent idempotent = handlerMethod.getMethod().getAnnotation(ApiIdempotent.class);
        /* 含有幂等性校验注解 */
        if (null != idempotent) {
            String tokenKey = acquireTokenKey(request);
            idempotentService.verify(tokenKey);
        }

        return true;
    }

    /**
     * 获取请求中的 token key
     * @param request 请求体
     * @return token key
     */
    private String acquireTokenKey(HttpServletRequest request) {
        String key;
        /*
         * 1. 现在 header 中获取 key
         * 2. 在 params 中获取 key
         */
        if (null == (key = request.getHeader(Constants.IDEMPOTENT_TOKEN_KEY))) {
            key = request.getParameter(Constants.IDEMPOTENT_TOKEN_KEY);
        }
        /* 没有携带幂等性处理 token key，抛出异常 */
        if (null == key) {
            throw new IdempotentException("Cannot acquire idempotent token key",
                    UnifyResultCode.PARAM_IS_BLANK);
        }
        if (log.isDebugEnabled()) {
            log.debug("Acquire token key [{}]", key);
        }
        return key;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
