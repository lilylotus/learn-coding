package cn.nihility.boot.interceptor;

import cn.nihility.boot.annotation.AutoIdempotent;
import cn.nihility.boot.exception.AuthenticationException;
import cn.nihility.boot.service.TokenServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;

/**
 * 请求幂等性拦截器
 * @author dandelion
 * @date 2020:06:27 20:00
 */
public class AutoIdempotentInterceptor implements HandlerInterceptor {

    private final static Logger log = LoggerFactory.getLogger(AutoIdempotentInterceptor.class);

    private TokenServiceImpl tokenService;

    public AutoIdempotentInterceptor(TokenServiceImpl tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("AutoIdempotentInterceptor -> preHandle");

        // 仅处理拦截的方法
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();

        if (method.isAnnotationPresent(AutoIdempotent.class)) {
            try {
                return tokenService.checkToken(request);
            } catch (Exception ex) {
                response.setStatus(401);
                request.setAttribute(WebUtils.ERROR_STATUS_CODE_ATTRIBUTE, 401);
                /*ResultVo vo = ResultVoUtil.error("幂等性校验 token 失败");
                writeResponseAsJson(response, JSON.toJSONString(vo));*/
                throw new AuthenticationException("幂等性校验 token 失败, " + ex.getMessage());
            }
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        log.info("AutoIdempotentInterceptor -> postHandle");
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        log.info("AutoIdempotentInterceptor -> afterCompletion");
    }

    /**
     * 返回 json 数据
     */
    private void writeResponseAsJson(HttpServletResponse response, String jsonMsg) {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=utf-8");
        try (PrintWriter writer = response.getWriter()) {
            writer.println(jsonMsg);
        } catch (IOException e) {
            log.error("write response json message error, message [{}]", jsonMsg, e);
        }
    }
}
