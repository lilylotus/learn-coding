package cn.nihility.common.util;

import cn.nihility.common.constant.Oauth2Constant;
import cn.nihility.common.constant.Oauth2ErrorEnum;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author nihilty
 * @date 2022/02/18 11:42
 */
public class Oauth2Utils {

    private Oauth2Utils() {
    }

    public static String codeGrantResponseType(HttpServletRequest request) {
        return request.getParameter(Oauth2Constant.RESPONSE_TYPE);
    }

    public static String clientId(HttpServletRequest request) {
        return request.getParameter(Oauth2Constant.CLIENT_ID);
    }

    public static String redirectUri(HttpServletRequest request) {
        return request.getParameter(Oauth2Constant.REDIRECT_URI);
    }

    public static String scope(HttpServletRequest request) {
        return request.getParameter(Oauth2Constant.SCOPE);
    }

    public static String state(HttpServletRequest request) {
        return request.getParameter(Oauth2Constant.STATE);
    }

    public static String buildErrorRedirectUrl(String redirectUrl, String errorCode,
                                               String errorDescription, String state) {
        Map<String, String> params = new HashMap<>(8);
        params.put("error", errorCode);
        params.put("error_description", errorDescription);
        params.put("state", state);
        return HttpRequestUtils.addUrlParams(redirectUrl, params);
    }

    public static String buildErrorRedirectUrl(String redirectUrl, Oauth2ErrorEnum errorEnum, String state) {
        return buildErrorRedirectUrl(redirectUrl, errorEnum.getCode(), errorEnum.getDescription(), state);
    }

}
