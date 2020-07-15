package cn.nihility.learn.interceptor;

import cn.nihility.learn.annotation.AutoIdempotent;
import cn.nihility.learn.common.ResultVo;
import cn.nihility.learn.idempotent.TokenServiceImpl;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;

/**
 * AutoIdemponentInterceptor
 *
 * @author dandelion
 * @date 2020-05-07 16:22
 */
@Slf4j
public class AutoIdempotentInterceptor implements HandlerInterceptor {

    private TokenServiceImpl tokenService;

    public AutoIdempotentInterceptor() {
    }

    public AutoIdempotentInterceptor(TokenServiceImpl tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("AutoIdempotentInterceptor -> preHandle");

        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();

        if (method.isAnnotationPresent(AutoIdempotent.class)) {
            try {
                // 等幂性校验
                return tokenService.checkToken(request);
            } catch (Exception ex) {
                ResultVo resultVo = ResultVo.getFailedResult(101, ex.getMessage());
                writeRespnseJson(response, JSON.toJSONString(resultVo));
                throw ex;
            }
        }

        // 最后返回 true
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }

    /**
     * 返回 json 数据
     * @param response
     * @param jsonMsg
     */
    private void writeRespnseJson(HttpServletResponse response, String jsonMsg) {

        PrintWriter writer = null;

        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=utf-8");

        try {
            writer = response.getWriter();
            writer.println(jsonMsg);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != writer) {
                writer.close();
            }
        }
    }
}
