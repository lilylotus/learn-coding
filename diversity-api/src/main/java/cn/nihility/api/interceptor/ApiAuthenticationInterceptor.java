package cn.nihility.api.interceptor;

import cn.nihility.api.annotation.ApiAuthenticationCheck;
import cn.nihility.api.exception.HttpRequestException;
import cn.nihility.common.constant.Constant;
import cn.nihility.common.constant.UnifyCodeMapping;
import cn.nihility.common.exception.JwtParseException;
import cn.nihility.common.util.HttpRequestUtils;
import cn.nihility.common.util.JwtUtils;
import cn.nihility.common.util.UnifyResultUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;

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

        ApiAuthenticationCheck check = findAnnotation((HandlerMethod) handler, ApiAuthenticationCheck.class);

        if (null != check && check.value()) {
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

    public static <A extends Annotation> A findAnnotation(HandlerMethod handlerMethod, Class<A> annotationType) {
        A method =
            AnnotationUtils.findAnnotation(handlerMethod.getMethod(), annotationType);
        A clazz =
            AnnotationUtils.findAnnotation(handlerMethod.getBeanType(), annotationType);
        return clazz == null ? method : clazz;
    }

}
