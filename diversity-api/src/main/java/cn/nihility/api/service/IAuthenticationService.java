package cn.nihility.api.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author nihility
 * @date 2022/02/21 10:22
 */
public interface IAuthenticationService {

    /**
     * 认证
     *
     * @return 认证成功后的重定向 url
     */
    String auth(HttpServletRequest request, HttpServletResponse response);

}
