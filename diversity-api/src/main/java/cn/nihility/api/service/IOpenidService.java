package cn.nihility.api.service;

import cn.nihility.api.dto.OidcTokenDto;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public interface IOpenidService {

    String authorize(HttpServletRequest request, HttpServletResponse response);

    OidcTokenDto codeConvertToken(HttpServletRequest request, HttpServletResponse response);

    Map<String, Object> userInfo(HttpServletRequest request, HttpServletResponse response);

    Map<String, Object> callback(HttpServletRequest request, HttpServletResponse response);

}
