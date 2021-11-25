package cn.nihility.api.config;

import cn.nihility.api.exception.HttpRequestException;
import cn.nihility.common.pojo.UnifyResult;
import cn.nihility.common.util.UnifyResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.util.WebUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private boolean debugStackTrace;

    public GlobalExceptionHandler(@Value("${api.unify.exception.debugStackTrace:false}") boolean debugStackTrace) {
        this.debugStackTrace = debugStackTrace;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> httpMessageNotReadableExceptionHandler(HttpMessageNotReadableException ex,
                                                                         WebRequest request) {
        final HttpHeaders headers = new HttpHeaders();
        return handleException(ex, UnifyResultUtil.failure(ex.getMessage()), headers, request);
    }

    @ExceptionHandler(HttpRequestException.class)
    public ResponseEntity<Object> httpRequestExceptionHandler(HttpRequestException ex, WebRequest request) {
        UnifyResult body = ex.getBody();
        if (null == body) {
            body = UnifyResultUtil.failure(ex.getMessage());
        }
        final HttpHeaders headers = new HttpHeaders();
        return handleExceptionInternal(ex, body, headers, ex.getStatus(), request);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<Object> httpRequestExceptionHandler(NullPointerException ex, WebRequest request) {
        UnifyResult body = UnifyResultUtil.failure("空指针异常");
        final HttpHeaders headers = new HttpHeaders();
        return handleException(ex, body, headers, request);
    }

    /**
     * 针对全局异常处理
     *
     * @param ex      异常
     * @param request 请求
     * @return 统一异常返回数据
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> exceptionHandler(Exception ex, WebRequest request) {
        final HttpHeaders headers = new HttpHeaders();
        return handleException(ex, UnifyResultUtil.failure(ex.getMessage()), headers, request);
    }

    /**
     * 异常类的统一处理
     */
    protected ResponseEntity<Object> handleException(Exception ex, Object body, HttpHeaders headers, WebRequest request) {
        return handleExceptionInternal(ex, body, headers, HttpStatus.OK, request);
    }

    /**
     * org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler#handleExceptionInternal(java.lang.Exception, java.lang.Object, org.springframework.http.HttpHeaders, org.springframework.http.HttpStatus, org.springframework.web.context.request.WebRequest)
     * <p>
     * A single place to customize the response body of all exception types.
     * <p>The default implementation sets the {@link WebUtils#ERROR_EXCEPTION_ATTRIBUTE}
     * request attribute and creates a {@link ResponseEntity} from the given
     * body, headers, and status.
     */
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
                                                             HttpStatus status, WebRequest request) {
        if (debugStackTrace && body instanceof UnifyResult) {
            ((UnifyResult) body).setStackTrace(formatStackTrace(ex.getStackTrace()));
        }
        if (HttpStatus.INTERNAL_SERVER_ERROR.equals(status)) {
            request.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, ex, RequestAttributes.SCOPE_REQUEST);
        }
        logger.error("Unify Handler Exception", ex);
        return new ResponseEntity<>(body, headers, status);
    }

    protected List<StackTraceElement> formatStackTrace(StackTraceElement[] traceElements) {
        if (null != traceElements) {
            return Stream.of(traceElements).collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }


}
