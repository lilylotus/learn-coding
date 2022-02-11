package cn.nihility.common.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 统一 WEB 返回格式
 *
 * @JsonInclude(JsonInclude.Include.NON_NULL) -> json 格式化忽略 null 字段
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UnifyResult<T> extends UnifyBaseResult {

    private static final long serialVersionUID = -2566544034307508982L;

    /**
     * 返回数据
     */
    private transient T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "UnifyResult{" +
            "data=" + data +
            '}';
    }

}
