package cn.nihility.common.util;

import cn.nihility.common.constant.UnifyCodeMapping;
import cn.nihility.common.pojo.UnifyResult;

public final class UnifyResultUtil {

    private UnifyResultUtil() {
    }

    /* ==================== 成功 ==================== */

    public static UnifyResult success(int code, Object data, String tip) {
        return new UnifyResult(code, tip, data);
    }

    public static UnifyResult success(int code, Object data) {
        return new UnifyResult(code, data);
    }

    public static UnifyResult success(Object data) {
        return success(UnifyCodeMapping.SUCCESS.getCode(), data);
    }

    public static UnifyResult success() {
        return success(UnifyCodeMapping.SUCCESS.getCode(), null);
    }

    public static UnifyResult success(UnifyCodeMapping result) {
        return success(result.getCode(), result.getMessage());
    }

    /* ==================== 失败/错误/异常 ==================== */

    public static UnifyResult failure(int code, String message) {
        return new UnifyResult(code, message);
    }

    public static UnifyResult failure(String tipMessage) {
        return new UnifyResult(UnifyCodeMapping.INTERNAL_SERVER_ERROR.getCode(), tipMessage);
    }

    public static UnifyResult failure(UnifyCodeMapping codeMapping) {
        return failure(codeMapping.getCode(), codeMapping.getMessage());
    }

}
