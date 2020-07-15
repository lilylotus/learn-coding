package cn.nihility.learn.common;

/**
 * ResultVo
 *
 * @author dandelion
 * @date 2020-05-07 16:28
 */
public class ResultVo {

    public static final ResultVo EMPTY = new ResultVo("empty", 0);

    private String message;
    private Integer code;
    private Object data;

    public ResultVo() {
    }

    public ResultVo(String message, Integer code) {
        this.message = message;
        this.code = code;
    }

    public static ResultVo getFailedResult(Integer code, String message) {
        return new ResultVo(message, code);
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

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
