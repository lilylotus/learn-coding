package cn.nihility.boot.aop;

import cn.nihility.boot.util.LogLevel;
import cn.nihility.boot.util.LoggerUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Calculate class methods aspect class.
 * @author muscari
 * @date 2019-06-13 23:13
 */
@Aspect
@Component
public class CalculateAspect {

    @Pointcut("execution(* cn.nihility.boot.aop.Calculate.*(..))")
    public void pointcut() {}

    @Before("pointcut()")
    public void before(JoinPoint joinPoint) {
        LoggerUtil.log(getClass(), LogLevel.INFO, "Before Aspect, method [{}], args [{}]",
                joinPoint.getSignature().getName(), Arrays.asList(joinPoint.getArgs()));
    }

    @After("pointcut()")
    public void after(JoinPoint joinPoint) {
        LoggerUtil.log(getClass(), LogLevel.INFO,  "After Aspect, method [{}], args [{}]",
                joinPoint.getSignature().getName(), Arrays.asList(joinPoint.getArgs()));
    }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        LoggerUtil.log(getClass(), LogLevel.INFO,  "Around Aspect, method [{}], args [{}]",
                proceedingJoinPoint.getSignature().getName(), Arrays.asList(proceedingJoinPoint.getArgs()));
        Object result = proceedingJoinPoint.proceed();
        LoggerUtil.log(getClass(), LogLevel.INFO,  "Around Aspect End, Result [{}]", result);
        return result;
    }

    @AfterReturning(pointcut = "pointcut()", returning = "result")
    public void afterReturning(JoinPoint joinPoint, Object result) {
        LoggerUtil.log(getClass(), LogLevel.INFO,  "AfterReturning Aspect, method [{}], args [{}], result [{}]",
                joinPoint.getSignature().getName(), Arrays.asList(joinPoint.getArgs()), result);
    }

    @AfterThrowing(pointcut = "pointcut()", throwing = "ex")
    public void afterThrowing(JoinPoint joinPoint, ArithmeticException ex) {
        LoggerUtil.log(getClass(), LogLevel.INFO,  "AfterThrowing Aspect, method [{}], args [{}], Exception [{}]",
                joinPoint.getSignature().getName(), Arrays.asList(joinPoint.getArgs()), ex);
    }

}
