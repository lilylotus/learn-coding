package cn.nihility.common.util;

import cn.nihility.common.constant.Oauth2ErrorEnum;
import cn.nihility.common.constant.OidcConstant;
import cn.nihility.common.exception.AuthenticationException;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class OidcUtils {

    private OidcUtils() {
    }

    public static String obtainResponseType(HttpServletRequest request) {
        return request.getParameter(OidcConstant.RESPONSE_TYPE_FILED);
    }

    public static String obtainClientId(HttpServletRequest request) {
        return request.getParameter(OidcConstant.CLIENT_ID_FIELD);
    }

    public static String obtainState(HttpServletRequest request) {
        return request.getParameter(OidcConstant.STATE_FIELD);
    }

    public static String obtainScope(HttpServletRequest request) {
        return request.getParameter(OidcConstant.SCOPE_FIELD);
    }

    public static String obtainRedirectUri(HttpServletRequest request) {
        return request.getParameter(OidcConstant.REDIRECT_URI_FIELD);
    }

    public static String validateAuthorizeParams(HttpServletRequest request) {

        String responseType = OidcUtils.obtainResponseType(request);
        String clientId = OidcUtils.obtainClientId(request);
        String state = OidcUtils.obtainState(request);
        String scope = OidcUtils.obtainScope(request);
        String redirectUri = OidcUtils.obtainRedirectUri(request);

        if (StringUtils.isBlank(redirectUri)) {
            throw new AuthenticationException("redirect_uri 参数不可为空");
        }
        if (StringUtils.isBlank(state)) {
            return OidcUtils.buildErrorRedirectUrl(redirectUri, Oauth2ErrorEnum.INVALID_REQUEST.getCode(),
                "state 不可为空", state);
        }
        if (StringUtils.isBlank(responseType)) {
            return OidcUtils.buildErrorRedirectUrl(redirectUri, Oauth2ErrorEnum.INVALID_REQUEST.getCode(),
                "response_type 不可为空", state);
        }
        if (!OidcConstant.AUTHORIZATION_CODE_VALUE.equals(responseType)
            && !OidcConstant.IMPLICIT_GRANT_VALUE.equals(responseType)) {
            return OidcUtils.buildErrorRedirectUrl(redirectUri, Oauth2ErrorEnum.UNSUPPORTED_RESPONSE_TYPE.getCode(),
                "不支持的 response_type 类型", state);
        }
        if (StringUtils.isBlank(scope)) {
            return OidcUtils.buildErrorRedirectUrl(redirectUri, Oauth2ErrorEnum.INVALID_REQUEST.getCode(),
                "scope 不可为空", state);
        }
        if (StringUtils.isBlank(clientId)) {
            return OidcUtils.buildErrorRedirectUrl(redirectUri, Oauth2ErrorEnum.INVALID_REQUEST.getCode(),
                "client_id 不可为空", state);
        }
        return null;
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
