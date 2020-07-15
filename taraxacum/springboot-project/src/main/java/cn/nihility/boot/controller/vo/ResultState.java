package cn.nihility.boot.controller.vo;

/**
 * @author dandelion
 * @date 2020:06:27 09:59
 */
public enum ResultState {
    SUCCESS("success", 0), ERROR("error", -1);

    private String message;
    private Integer code;

    ResultState(String message, Integer code) {
        this.message = message;
        this.code = code;
    }

    public static String getMessage(int code) {
        for (ResultState state : ResultState.values()) {
            if (state.getCode() == code) {
                return state.getMessage();
            }
        }
        return null;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
