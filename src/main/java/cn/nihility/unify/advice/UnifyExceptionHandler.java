package cn.nihility.unify.advice;

import cn.nihility.unify.exception.UnifyException;
import cn.nihility.unify.pojo.UnifyResultError;
import cn.nihility.unify.pojo.UnifyResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.util.WebUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 统一异常处理
 */
@RestControllerAdvice
public class UnifyExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(UnifyExceptionHandler.class);

    private final Boolean exceptionDebug;

    public UnifyExceptionHandler(@Value("${unify.exception.debug}") Boolean exceptionDebug) {
        this.exceptionDebug = exceptionDebug;
    }

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<UnifyResultError> exceptionHandler(Exception ex, WebRequest request) {
        HttpHeaders headers = new HttpHeaders();
        if (ex instanceof UnifyException) {
            return this.handleUnifyResultException((UnifyException) ex, headers, request);
        }
        return this.handleException(ex, headers, request);
    }

    /**
     * 对ResultException类返回返回结果的处理
     */
    protected ResponseEntity<UnifyResultError> handleUnifyResultException(UnifyException ex, HttpHeaders headers, WebRequest request) {
        return this.handleExceptionInternal(ex, UnifyResultUtil.failure(ex.getResultCode(), ex.getMessage()),
                headers, ex.getResultCode().getHttpStatus(), request);
    }

    /**
     * 异常类的统一处理
     */
    protected ResponseEntity<UnifyResultError> handleException(Exception ex, HttpHeaders headers, WebRequest request) {
        return this.handleExceptionInternal(ex, UnifyResultUtil.failure(ex.getMessage()),
                headers, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    /**
     * org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler#handleExceptionInternal(java.lang.Exception, java.lang.Object, org.springframework.http.HttpHeaders, org.springframework.http.HttpStatus, org.springframework.web.context.request.WebRequest)
     * <p>
     * A single place to customize the response body of all exception types.
     * <p>The default implementation sets the {@link WebUtils#ERROR_EXCEPTION_ATTRIBUTE}
     * request attribute and creates a {@link ResponseEntity} from the given
     * body, headers, and status.
     */
    protected ResponseEntity<UnifyResultError> handleExceptionInternal(
            Exception ex, UnifyResultError body, HttpHeaders headers, HttpStatus status, WebRequest request) {
        if (HttpStatus.INTERNAL_SERVER_ERROR.equals(status)) {
            request.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, ex, WebRequest.SCOPE_REQUEST);
        }
        if (Boolean.TRUE.equals(exceptionDebug)) {
            body.setDebug(formatStackTrace(ex.getStackTrace()));
        }
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
