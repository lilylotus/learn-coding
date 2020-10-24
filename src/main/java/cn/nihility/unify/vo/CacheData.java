package cn.nihility.unify.vo;

import java.io.Serializable;

/**
 * 模拟缓存操作的数据
 */
public class CacheData implements Serializable {

    private static final long serialVersionUID = 1061386985282237853L;
    private String desc;
    private Long id;

    public CacheData() {
    }

    public CacheData(String desc, Long id) {
        this.desc = desc;
        this.id = id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "CacheData(" +
                "desc='" + desc + '\'' +
                ",id=" + id +
                ')';
    }
}
