package cn.nihility.api.interceptor;

import cn.nihility.api.annotation.ApiAuthenticationCheck;
import cn.nihility.api.exception.HttpRequestException;
import cn.nihility.common.constant.Constant;
import cn.nihility.common.constant.UnifyCodeMapping;
import cn.nihility.common.exception.JwtParseException;
import cn.nihility.common.util.HttpRequestUtils;
import cn.nihility.common.util.JwtUtils;
import cn.nihility.common.util.UnifyResultUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 接口身份认证校验拦截器
 *
 * @author nihility
 */
public class ApiAuthenticationInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        /* 处理拦截方法 */
        if (!(handler instanceof HandlerMethod)) {
            return false;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        ApiAuthenticationCheck methodCheck = handlerMethod.getMethod().getAnnotation(ApiAuthenticationCheck.class);
        ApiAuthenticationCheck clazzCheck = handlerMethod.getBeanType().getAnnotation(ApiAuthenticationCheck.class);
        boolean check = (null != methodCheck && methodCheck.value()) || (null != clazzCheck && clazzCheck.value());
        if (check) {
            try {
                String token = HttpRequestUtils.bearerTokenValue(HttpRequestUtils.obtainRequestValue(
                    Constant.AUTHENTICATION_TOKEN_KEY, request));
                JwtUtils.verifyJwtToken(token);
            } catch (JwtParseException ex) {
                throw new HttpRequestException(HttpStatus.UNAUTHORIZED,
                    UnifyResultUtils.failure(UnifyCodeMapping.UNAUTHORIZED.getCode(), ex.getMessage()));
            }
        }

        return true;
    }

}
