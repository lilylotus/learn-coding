package cn.nihility.api.service;

import cn.nihility.api.dto.Oauth2Response;
import cn.nihility.common.exception.AuthenticationException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author nihility
 * @date 2022/02/18 11:24
 */
public interface IOauth2Service {

    /**
     * OAuth2.0 授权
     *
     * @param request  请求
     * @param response 响应
     * @return 重定向地址
     */
    String authorize(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException;

    Oauth2Response createCodeGrantToken(HttpServletRequest request, HttpServletResponse response);

    Map<String, Object> userInfo(HttpServletRequest request, HttpServletResponse response);

    Map<String, Object> rec(HttpServletRequest request, HttpServletResponse response);

}
