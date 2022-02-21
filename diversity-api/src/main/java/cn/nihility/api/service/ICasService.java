package cn.nihility.api.service;

import cn.nihility.api.dto.CasResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author nihility
 * @date 2022/02/21 15:53
 */
public interface ICasService {

    String casLogin(HttpServletRequest request, HttpServletResponse response);

    CasResponse serviceValidate(HttpServletRequest request, HttpServletResponse response);

    Map<String, Object> rec(HttpServletRequest request, HttpServletResponse response);

}
