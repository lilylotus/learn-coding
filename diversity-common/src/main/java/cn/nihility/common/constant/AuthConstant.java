package cn.nihility.common.constant;

/**
 * @author nihility
 * @date 2022/02/21 10:17
 */
public class AuthConstant {

    private AuthConstant() {
    }

    /**
     * 重定向参数
     */
    public static final String REDIRECT_TAG = "redirect";
    /**
     * 登录表单用户名
     */
    public static final String LOGIN_FORM_USERNAME = "username";
    /**
     * 登录表单密码
     */
    public static final String LOGIN_FORM_PASSWORD = "password";


    public static final String AUTHORIZATION_KEY = "Authorization";

    public static final long TOKEN_TTL = 7200L;

    public static final long SESSION_TTL = 7200L;

}
