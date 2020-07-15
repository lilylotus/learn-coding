package cn.nihility.boot.exception;

import cn.nihility.boot.controller.vo.ResultErrorInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.WebUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 统一异常信息
 * @author dandelion
 * @date 2020:06:27 11:18
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
public class ErrorInfoBuilder implements HandlerExceptionResolver, Ordered {

    private final static String ERROR_NAME = "error";
    // 错误配置
    private ErrorProperties errorProperties;

    /**
     * 错误构造函数， 传递配置属性， server.xx -> server.error.xx
     */
    public ErrorInfoBuilder(ServerProperties serverProperties) {
        this.errorProperties = serverProperties.getError();
    }

    /**
     * 构造错误信息， ErrorInfo
     */
    public ResultErrorInfo getErrorInfo(HttpServletRequest request) {
        return getErrorInfo(request, getError(request));
    }

    /**
     * 构建错误信息.(ErrorInfo)
     */
    public ResultErrorInfo getErrorInfo(HttpServletRequest request, Throwable error) {
        ResultErrorInfo errorInfo = new ResultErrorInfo();
        errorInfo.setTime(LocalDateTime.now().toString());
        errorInfo.setUrl(request.getRequestURL().toString());
        errorInfo.setError(error.toString());
        errorInfo.setStatusCode(getHttpStatus(request).value());
        errorInfo.setReasonPhrase(getHttpStatus(request).getReasonPhrase());
        errorInfo.setStackTrace(getStackTraceInfo(error, isIncludeStackTrace(request)));
        return errorInfo;
    }

    /**
     * 获取错误 (ERROR/EXCEPTION)
     * @see org.springframework.boot.web.servlet.error.DefaultErrorAttributes#addErrorDetails(Map, WebRequest, boolean)
     */
    private Throwable getError(HttpServletRequest request) {
        // 根据 HandlerExceptionResolver 接口方法来获取错误.
        Throwable error = (Throwable) request.getAttribute(ERROR_NAME);
        // 根据Request对象获取错误.
        if (error == null) {
            error = (Throwable) request.getAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE);
        }
        // 当获取错误非空, 取出 RootCause
        if (error != null) {
            while (error instanceof ServletException && error.getCause() != null) {
                error = error.getCause();
            }
        } else { //当获取错误为null,此时我们设置错误信息即可.
            String message = (String) request.getAttribute(WebUtils.ERROR_MESSAGE_ATTRIBUTE);
            if (StringUtils.isEmpty(message)) {
                HttpStatus status = getHttpStatus(request);
                message = "Unknown Exception But " + status.value() + " : " + status.getReasonPhrase();
            }
            error = new Exception(message);
        }
        return error;
    }

    /**
     * 获取通信状态(HttpStatus)
     * @see AbstractErrorController #getStatus
     */
    private HttpStatus getHttpStatus(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute(WebUtils.ERROR_STATUS_CODE_ATTRIBUTE);
        try {
            return statusCode != null ? HttpStatus.valueOf(statusCode) : HttpStatus.INTERNAL_SERVER_ERROR;
        } catch (Exception ex) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

    /**
     * 获取堆栈轨迹(StackTrace)
     *
     * @see DefaultErrorAttributes  #addStackTrace
     */
    private String getStackTraceInfo(Throwable error, boolean flag) {
        if (!flag) {
            return "omitted";
        }
        StringWriter stackTrace = new StringWriter();
        error.printStackTrace(new PrintWriter(stackTrace));
        stackTrace.flush();
        return stackTrace.toString();
    }


    /**
     * 判断是否包含堆栈轨迹.(isIncludeStackTrace)
     *
     * @see BasicErrorController #isIncludeStackTrace
     */
    private boolean isIncludeStackTrace(HttpServletRequest request) {

        //读取错误配置(server.error.include-stacktrace=NEVER)
        ErrorProperties.IncludeStacktrace includeStacktrace = errorProperties.getIncludeStacktrace();

        // 情况1：若IncludeStacktrace为ALWAYS
        if (includeStacktrace == ErrorProperties.IncludeStacktrace.ALWAYS) {
            return true;
        }
        // 情况2：若请求参数含有trace
        if (includeStacktrace == ErrorProperties.IncludeStacktrace.ON_TRACE_PARAM) {
            String parameter = request.getParameter("trace");
            return parameter != null && !"false".equals(parameter.toLowerCase());
        }
        // 请求 header 中有 trace:true
        return "enable".equals(request.getHeader("trace"));
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    /**
     * 保存错误/异常
     */
    @Override
    public ModelAndView resolveException(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) {
        httpServletRequest.setAttribute(ERROR_NAME, e);
        return null;
    }

    public static String getErrorName() {
        return ERROR_NAME;
    }

    public ErrorProperties getErrorProperties() {
        return errorProperties;
    }

    public void setErrorProperties(ErrorProperties errorProperties) {
        this.errorProperties = errorProperties;
    }
}
