package cn.nihility.boot.controller.handler;

import cn.nihility.boot.controller.vo.ResultVo;
import cn.nihility.boot.exception.AuthenticationException;
import cn.nihility.boot.exception.ErrorInfoBuilder;
import cn.nihility.boot.util.ResultVoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;

/**
 * @author dandelion
 * @date 2020:06:27 11:33
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final static Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    // 错误页面
    private final static String DEFAULT_ERROR_VIEW = "error";

    private ErrorInfoBuilder errorInfoBuilder;

    public GlobalExceptionHandler(ErrorInfoBuilder errorInfoBuilder) {
        this.errorInfoBuilder = errorInfoBuilder;
    }

    @ExceptionHandler(value = AuthenticationException.class)
    public ResultVo javaExceptionHandler(Exception ex, HttpServletRequest request) {
        LOGGER.error("Catch AuthenticationException error msg ", ex);
        return ResultVoUtil.error(errorInfoBuilder.getErrorInfo(request, ex));
    }


    /*@ExceptionHandler(value = Exception.class)
    public ResultVo innerExceptionHandler(Exception ex) {
        LOGGER.error("Catch Exception error msg ", ex);
        return ResultVoUtil.error("Meet Error : " + ex.getMessage());
    }*/

    @ExceptionHandler(value = Exception.class)
    public Object innerExceptionHandlerMsg(Exception ex, HttpServletRequest request) {
        LOGGER.error("Catch Exception error msg ", ex);

        if (ex.getClass().isAssignableFrom(SQLException.class)) {
            return new ModelAndView(DEFAULT_ERROR_VIEW, "errorInfo", errorInfoBuilder.getErrorInfo(request, ex));
        }

        return ResultVoUtil.error("Meet Error", errorInfoBuilder.getErrorInfo(request, ex));
    }

    private boolean isAjaxRequest(HttpServletRequest request) {
        return "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
    }

    public ErrorInfoBuilder getErrorInfoBuilder() {
        return errorInfoBuilder;
    }

    public void setErrorInfoBuilder(ErrorInfoBuilder errorInfoBuilder) {
        this.errorInfoBuilder = errorInfoBuilder;
    }
}
