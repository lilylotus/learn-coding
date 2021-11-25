package cn.nihility.api.interceptor;

import cn.nihility.api.annotation.ApiAuthenticationCheck;
import cn.nihility.api.constant.Constant;
import cn.nihility.api.exception.HttpRequestException;
import cn.nihility.common.constant.UnifyCodeMapping;
import cn.nihility.common.exception.JwtParseException;
import cn.nihility.common.util.JwtUtil;
import cn.nihility.common.util.UnifyResultUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 接口身份认证校验拦截器
 */
public class ApiAuthenticationCheckInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        /* 处理拦截方法 */
        if (!(handler instanceof HandlerMethod)) {
            return false;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        ApiAuthenticationCheck apiCheck = handlerMethod.getMethod().getAnnotation(ApiAuthenticationCheck.class);
        ApiAuthenticationCheck clazzCheck = handlerMethod.getBeanType().getAnnotation(ApiAuthenticationCheck.class);
        if ((null != apiCheck && apiCheck.value()) ||
            (null != clazzCheck && clazzCheck.value())) {
            try {
                JwtUtil.verifyJwtToken(obtainAuthenticationToken(request));
            } catch (JwtParseException ex) {
                throw new HttpRequestException(HttpStatus.UNAUTHORIZED,
                    UnifyResultUtil.failure(UnifyCodeMapping.UNAUTHORIZED.getCode(), ex.getMessage()));
            }
        }

        return true;
    }

    private String obtainAuthenticationToken(HttpServletRequest request) {
        String token = request.getHeader(Constant.AUTHENTICATION_TOKEN_KEY);
        if (StringUtils.isBlank(token)) {
            token = request.getParameter(Constant.AUTHENTICATION_TOKEN_KEY);
        }
        if (StringUtils.isBlank(token)) {
            for (Cookie cookie : request.getCookies()) {
                if (Constant.AUTHENTICATION_TOKEN_KEY.equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }
        return StringUtils.isBlank(token) ? null :
            token.replace(Constant.AUTHENTICATION_BEARER_TOKEN_PREFIX, "");
    }
}
