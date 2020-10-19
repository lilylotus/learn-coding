package cn.nihility.unify.aspect;

import cn.nihility.unify.pojo.UnifyRequestErrorInfo;
import cn.nihility.unify.pojo.UnifyRequestInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
public class UnifyRequestAspect {

    private static final Logger log = LoggerFactory.getLogger(UnifyRequestAspect.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

//    @Pointcut("execution(public * cn.nihility.unify.controller.*.*(..)) || execution(public * cn.nihility.unify..*.*Controller.*(..))")
    @Pointcut("execution(public * cn.nihility.unify..*.*Controller.*(..))")
    public void controllerPointCut() {}

    @Around("controllerPointCut()")
    public Object aroundAspect(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = proceedingJoinPoint.proceed();
        long duration = System.currentTimeMillis() - start;

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        UnifyRequestInfo requestInfo = new UnifyRequestInfo();
        if (null != attributes) {
            HttpServletRequest request = attributes.getRequest();
            requestInfo.setIp(request.getRemoteAddr());
            requestInfo.setUrl(request.getRequestURL().toString());
            requestInfo.setHttpMethod(request.getMethod());
        }

        requestInfo.setClassMethod(String.format("%s.%s", proceedingJoinPoint.getSignature().getDeclaringTypeName(),
                proceedingJoinPoint.getSignature().getName()));
        requestInfo.setRequestParams(getRequestParamsByJoinPoint(proceedingJoinPoint));
        requestInfo.setResult(result);
        requestInfo.setTimeCost(duration);

        log.info("Request Info [{}]", OBJECT_MAPPER.writeValueAsString(requestInfo));

        return result;
    }

    @AfterThrowing(value = "controllerPointCut()", throwing = "ex")
    public void afterThrowAspect(JoinPoint joinPoint, RuntimeException ex) throws JsonProcessingException {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        UnifyRequestErrorInfo errorInfo = new UnifyRequestErrorInfo();
        if (null != attributes) {
            HttpServletRequest request = attributes.getRequest();
            errorInfo.setIp(request.getRemoteAddr());
            errorInfo.setUrl(request.getRequestURL().toString());
            errorInfo.setHttpMethod(request.getMethod());
        }
        errorInfo.setClassMethod(String.format("%s.%s", joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName()));
        errorInfo.setRequestParams(getRequestParamsByJoinPoint(joinPoint));
        errorInfo.setException(ex);

        log.info("Request Error Info [{}]", OBJECT_MAPPER.writeValueAsString(errorInfo));
    }

    private Map<String, Object> getRequestParamsByJoinPoint(JoinPoint joinPoint) {
        // 参数名
        String[] paramNames = ((MethodSignature) joinPoint.getSignature()).getParameterNames();
        // 参数值
        Object[] paramValues = joinPoint.getArgs();

        return buildRequestParam(paramNames, paramValues);
    }

    private Map<String, Object> buildRequestParam(String[] paramNames, Object[] paramValues) {
        Map<String, Object> requestParams = new HashMap<>();

        if (null != paramNames) {
            for (int i = 0; i < paramNames.length; i++) {
                Object value = paramValues[i];
                // 如果是文件对象
                if (value instanceof MultipartFile) {
                    MultipartFile file = (MultipartFile) value;
                    // 获取文件名
                    value = file.getOriginalFilename();
                }

                requestParams.put(paramNames[i], value);
            }
        }

        return requestParams;
    }

}
