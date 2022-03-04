package cn.nihility.api.config;

import cn.nihility.api.exception.HttpRequestException;
import cn.nihility.common.constant.UnifyCodeMapping;
import cn.nihility.common.exception.BusinessException;
import cn.nihility.common.pojo.UnifyBaseResult;
import cn.nihility.common.util.UnifyResultUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.util.WebUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author nihility
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private boolean debugStackTrace;

    public GlobalExceptionHandler(@Value("${api.unify.exception.debugStackTrace:false}") boolean debugStackTrace) {
        this.debugStackTrace = debugStackTrace;
    }

    private String parseBindFieldErrorsMessage(BindingResult bindingResult) {
        StringJoiner joiner = new StringJoiner("; ");
        List<FieldError> errors = Optional.of(bindingResult.getFieldErrors()).orElse(new ArrayList<>(0));
        errors.forEach(e -> joiner.add(e.getField() + ":" + e.getDefaultMessage()));
        return joiner.toString();
    }

    private String parseBindAllErrorsMessage(BindingResult bindingResult) {
        StringJoiner joiner = new StringJoiner("; ");
        List<ObjectError> errors = Optional.of(bindingResult.getAllErrors()).orElse(new ArrayList<>(0));
        errors.forEach(e -> joiner.add(e.getDefaultMessage()));
        return joiner.toString();
    }

    /**
     * 拦截表单参数校验
     *
     * @param ex 表单参数异常
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({BindException.class})
    public UnifyBaseResult bindException(BindException ex) {
        BindingResult bindingResult = ex.getBindingResult();

        String bindFailMsg = "参数绑定失败:" + parseBindAllErrorsMessage(bindingResult);

        //logger.error("请求参数绑定异常：[{}]", bindFailMsg, ex);
        return UnifyResultUtils.failure(UnifyCodeMapping.PARAM_BIND_FAILED.getCode(), bindFailMsg);
    }

    /**
     * 拦截方法参数校验异常
     *
     * @param ex 方法参数校验异常
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public UnifyBaseResult bindException(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();

        String validateFailMsg = parseBindFieldErrorsMessage(bindingResult);

        //logger.error("请求参数校验异常：[{}]", validateFailMsg, ex);
        return UnifyResultUtils.failure(UnifyCodeMapping.PARAM_VALIDATE_FAILED.getCode(), validateFailMsg);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<UnifyBaseResult> httpMessageNotReadableExceptionHandler(HttpMessageNotReadableException ex) {
        return handleExceptionInternal(ex, UnifyResultUtils.failure(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    private UnifyBaseResult buildExceptionBody(RuntimeException ex, UnifyBaseResult body) {
        return null == body ? UnifyResultUtils.failure(ex.getMessage()) : body;
    }

    /**
     * 拦截 http 请求处理异常
     *
     * @param ex http 请求异常
     */
    @ExceptionHandler(HttpRequestException.class)
    public ResponseEntity<UnifyBaseResult> httpRequestExceptionHandler(HttpRequestException ex) {
        UnifyBaseResult body = buildExceptionBody(ex, ex.getBody());
        HttpStatus httpStatus = ex.getHttpStatus();
        if (null == httpStatus) {
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return handleExceptionInternal(ex, body, httpStatus);
    }

    /**
     * 拦截业务异常
     *
     * @param ex 业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<UnifyBaseResult> businessExceptionHandler(BusinessException ex) {
        UnifyBaseResult body = buildExceptionBody(ex, ex.getBody());
        HttpStatus httpStatus = HttpStatus.resolve(ex.getStatusCode());
        if (null == httpStatus) {
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return handleExceptionInternal(ex, body, httpStatus);
    }

    /**
     * 拦截、处理空指针异常
     *
     * @param ex NullPointerException
     */
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<UnifyBaseResult> httpRequestExceptionHandler(NullPointerException ex) {
        return handleExceptionInternal(ex, UnifyResultUtils.failure("空指针异常"), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 针对全局异常处理
     *
     * @param ex 异常
     * @return 统一异常返回数据
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<UnifyBaseResult> exceptionHandler(Exception ex) {
        return handleExceptionInternal(ex, UnifyResultUtils.failure(ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler#handleExceptionInternal(java.lang.Exception, java.lang.Object, org.springframework.http.HttpHeaders, org.springframework.http.HttpStatus, org.springframework.web.context.request.WebRequest)
     * <p>
     * A single place to customize the response body of all exception types.
     * <p>The default implementation sets the {@link WebUtils#ERROR_EXCEPTION_ATTRIBUTE}
     * request attribute and creates a {@link ResponseEntity} from the given
     * body, headers, and status.
     */
    protected ResponseEntity<UnifyBaseResult> handleExceptionInternal(Exception ex, UnifyBaseResult body, HttpStatus status) {
        if (debugStackTrace) {
            body.setStackTrace(formatStackTrace(ex.getStackTrace()));
        }
        logger.error("Unify Handler Exception", ex);
        return new ResponseEntity<>(body, status);
    }

    protected List<StackTraceElement> formatStackTrace(StackTraceElement[] traceElements) {
        if (null != traceElements) {
            return Stream.of(traceElements).collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

}
