package cn.nihility.common.entity;

import java.util.Map;

/**
 * @author nihility
 * @date 2022/02/21 10:23
 */
public class UserInfo {

    private String userId;

    private String userName;

    private Map<String, Object> ext;

    /* ============================== getter/setter ============================== */

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Map<String, Object> getExt() {
        return ext;
    }

    public void setExt(Map<String, Object> ext) {
        this.ext = ext;
    }
    
}
