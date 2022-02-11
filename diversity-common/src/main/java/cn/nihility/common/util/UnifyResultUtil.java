package cn.nihility.common.util;

import cn.nihility.common.constant.UnifyCodeMapping;
import cn.nihility.common.pojo.UnifyBaseResult;
import cn.nihility.common.pojo.UnifyResult;

import java.util.List;

public final class UnifyResultUtil {

    private UnifyResultUtil() {
    }

    /* ==================== 成功 ==================== */

    public static <T> UnifyResult<T> success(Integer code, T data, String message) {
        UnifyResult<T> result = new UnifyResult<>();
        result.setCode(code);
        result.setData(data);
        result.setMessage(message);
        result.setTimestamp(System.currentTimeMillis());
        return result;
    }

    public static <T> UnifyResult<T> success(T data, String message) {
        return success(UnifyCodeMapping.SUCCESS.getCode(), data, message);
    }

    public static <T> UnifyResult<T> success(T data) {
        return success(UnifyCodeMapping.SUCCESS.getCode(), data, null);
    }


    public static UnifyResult<Object> success() {
        return success(UnifyCodeMapping.SUCCESS.getCode(), null, null);
    }

    /* ==================== 失败/错误/异常 ==================== */

    public static UnifyBaseResult failure(Integer code, String message, List<StackTraceElement> stackTrace) {
        UnifyBaseResult result = new UnifyBaseResult();
        result.setCode(code);
        result.setMessage(message);
        result.setStackTrace(stackTrace);
        result.setTimestamp(System.currentTimeMillis());
        return result;
    }

    public static UnifyBaseResult failure(Integer code, String message) {
        return failure(code, message, null);
    }

    public static UnifyBaseResult failure(UnifyCodeMapping unifyCode, List<StackTraceElement> stackTrace) {
        return failure(unifyCode.getCode(), unifyCode.getMessage(), stackTrace);
    }

    public static UnifyBaseResult failure(UnifyCodeMapping unifyCode) {
        return failure(unifyCode, null);
    }

    public static UnifyBaseResult failure(String message, List<StackTraceElement> stackTrace) {
        return failure(UnifyCodeMapping.INTERNAL_SERVER_ERROR.getCode(), message, stackTrace);
    }

    public static UnifyBaseResult failure(String message) {
        return failure(message, null);
    }

}
