package cn.nihility.unify.interceptor;

import cn.nihility.unify.annotaion.SkipAuthentication;
import cn.nihility.unify.annotaion.VerifyAuthentication;
import cn.nihility.unify.exception.UnifyException;
import cn.nihility.unify.pojo.UnifyResultCode;
import cn.nihility.unify.util.JWTUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * 身份认证拦截器，拦截是否登录
 */
public class AuthenticationInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        /* 放行非方法的处理器 */
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;

        Class<?> clazzType = handlerMethod.getBeanType();
        Method execMethod = handlerMethod.getMethod();
        /* skip -> true: 跳过认证，false: 需要认证 */
        boolean skip = false;
        // 类级别跳过认证注解
        if (clazzType.isAnnotationPresent(SkipAuthentication.class)) {
            skip = clazzType.getAnnotation(SkipAuthentication.class).skip();
            if (log.isDebugEnabled()) {
                log.debug("Class Skip Authentication Annotation [{}]", skip);
            }
        }
        // 方法级别跳过认证注解
        if (execMethod.isAnnotationPresent(SkipAuthentication.class)) {
            skip = execMethod.getAnnotation(SkipAuthentication.class).skip();
            if (log.isDebugEnabled()) {
                log.debug("Method Skip Authentication Annotation [{}]", skip);
            }
        }

        /* verify -> true : 认证，false : 取消认证
        * 按理应该是所有的方法都应该默认需要登陆认证，此处为了测试，仅有需要认证注解的类或方法需要认证
        * */
        boolean verify = false;
        // 类级别需要认证注解
        if (clazzType.isAnnotationPresent(VerifyAuthentication.class)) {
            verify = clazzType.getAnnotation(VerifyAuthentication.class).verify();
            if (log.isDebugEnabled()) {
                log.debug("Class Verify Authentication Annotation [{}]", verify);
            }
        }
        // 方法需要认证注解
        if (execMethod.isAnnotationPresent(VerifyAuthentication.class)) {
            verify = execMethod.getAnnotation(VerifyAuthentication.class).verify();
            if (log.isDebugEnabled()) {
                log.debug("Method Verify Authentication Annotation [{}]", verify);
            }
        }

        if (verify && !skip) {
            // 认证校验，登录 token 校验
            String authToken = request.getHeader("Authorization");
            if (log.isDebugEnabled()) {
                log.debug("Authentication Token [{}]", authToken);
            }
            if (StringUtils.isBlank(authToken)) {
                throw new UnifyException("认证 token 为空", UnifyResultCode.UNAUTHORIZED);
            }
            // 去掉 [Bearer ] 前缀
            if (!JWTUtil.verifyJwtToken(authToken.replace("Bearer ", ""))) {
                throw new UnifyException("认证 token 过期", UnifyResultCode.UNAUTHORIZED);
            }
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

}
