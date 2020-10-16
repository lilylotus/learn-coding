package cn.nihility.unify.pojo;

public class UnifyResultUtil {

    /**
     * 成功
     */
    public static UnifyResult success() {
        return new UnifyResult(UnifyResultCode.SUCCESS);
    }

    public static UnifyResult success(Object data) {
        return new UnifyResult(UnifyResultCode.SUCCESS, data);
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
