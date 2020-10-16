package cn.nihility.unify.aspect;

import cn.nihility.unify.pojo.UnifyRequestInfo;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
public class UnifyRequestAspect {

    private static final Logger log = LoggerFactory.getLogger(UnifyRequestAspect.class);

    @Pointcut("execution(public * cn.nihility.unify.controller.*(..)) || execution(public * cn.nihility.unify..*.*Controller.*(..))")
    public void controllerPointCut() {}

    @Around("controllerPointCut()")
    public Object aroundAspect(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = proceedingJoinPoint.proceed();
        long duration = System.currentTimeMillis() - start;

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        UnifyRequestInfo requestInfo = new UnifyRequestInfo();
        requestInfo.setIp(request.getRemoteAddr());
        requestInfo.setUrl(request.getRequestURL().toString());
        requestInfo.setHttpMethod(request.getMethod());
        requestInfo.setClassMethod(String.format("%s.%s", proceedingJoinPoint.getSignature().getDeclaringTypeName(),
                proceedingJoinPoint.getSignature().getName()));
        requestInfo.setRequestParams(getRequestParamsByProceedingJoinPoint(proceedingJoinPoint));
        requestInfo.setResult(result);
        requestInfo.setTimeCost(System.currentTimeMillis() - start);
        LOGGER.info("Request Info      : {}", JSON.toJSONString(requestInfo));

        return result;
    }

}
