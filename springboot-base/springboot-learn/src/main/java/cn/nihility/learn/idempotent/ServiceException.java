package cn.nihility.learn.idempotent;

/**
 * ServiceException
 *
 * @author dandelion
 * @date 2020-05-07 16:09
 */
public class ServiceException extends RuntimeException {

    private static final long serialVersionUID = 8174492997512975817L;
    private String code;
    private String msg;

    public ServiceException() {
    }

    public ServiceException(String msg) {
        super(msg);
        this.msg = msg;
    }

    public ServiceException(String code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
