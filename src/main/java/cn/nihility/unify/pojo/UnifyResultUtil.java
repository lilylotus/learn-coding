package cn.nihility.unify.pojo;

public class UnifyResultUtil {

    /**
     * 成功
     */
    public static UnifyResult success() {
        return success(UnifyResultCode.SUCCESS);
    }

    public static UnifyResult success(UnifyResultCode resultCode, String tipMessage) {
        return success(resultCode, tipMessage, null);
    }

    public static UnifyResult success(UnifyResultCode resultCode) {
        return success(resultCode, null);
    }

    public static UnifyResult success(Object data) {
        return success(UnifyResultCode.SUCCESS, data);
    }

    public static UnifyResult success(UnifyResultCode resultCode, Object data) {
        return new UnifyResult(resultCode, data);
    }

    public static UnifyResult success(UnifyResultCode resultCode, String tipMessage, Object data) {
        return new UnifyResult(resultCode, tipMessage, data);
    }

    /**
     * 失败/错误/异常
     */
    public static UnifyResultError failure(UnifyResultCode resultCode) {
        return new UnifyResultError(resultCode);
    }

    public static UnifyResultError failure(String tipMessage) {
        return new UnifyResultError(tipMessage);
    }

    public static UnifyResultError failure(UnifyResultCode resultCode, String tipMessage) {
        return new UnifyResultError(resultCode, tipMessage);
    }

}
