package cn.nihility.boot.interceptor;

import cn.nihility.boot.annotation.LoginToken;
import cn.nihility.boot.annotation.PassToken;
import cn.nihility.boot.exception.AuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * 拦截 token
 * @author dandelion
 * @date 2020:06:27 10:51
 */
public class AuthenticationInterceptor implements HandlerInterceptor, Ordered {

    private final static Logger log = LoggerFactory.getLogger(AuthenticationInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("AuthenticationInterceptor -> preHandle");

        String token = request.getHeader("token");
        //  不是映射到的方法直接通过
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();

        // 检查是否有 @PassToken 注解
        if (method.isAnnotationPresent(PassToken.class)) {
            return method.getAnnotation(PassToken.class).required();
        }

        // 检查是否有权限注解
        if (method.isAnnotationPresent(LoginToken.class)) {
            boolean required = method.getAnnotation(LoginToken.class).required();
            if (required) {
                // 执行认证
                if (null == token || "".equals(token.trim())) {
                    throw new AuthenticationException("无 token ，请登录");
                }

                log.info("Authentication token [{}]", token);

                return true;
            }
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        log.info("AuthenticationInterceptor -> postHandle");
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        log.info("AuthenticationInterceptor -> afterCompletion");
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE + 10;
    }
}
