package cn.nihility.unify.advice;

import cn.nihility.unify.annotaion.UnifyResponse;
import cn.nihility.unify.pojo.UnifyResult;
import cn.nihility.unify.pojo.UnifyResultError;
import cn.nihility.unify.pojo.UnifyResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 统一封装返回体 (response body)
 */
@RestControllerAdvice
public class UnifyResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    private static final Logger log = LoggerFactory.getLogger(UnifyResponseBodyAdvice.class);

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return AnnotatedElementUtils.hasAnnotation(returnType.getContainingClass(), UnifyResponse.class) ||
                returnType.hasMethodAnnotation(UnifyResponse.class);
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {

        if (body instanceof UnifyResult || body instanceof UnifyResultError) {
            return body;
        }
        /*
        * selectedContentType : application/json
        * type : application
        * subtype : json
        * */

        /*
        * selectedConverterType: org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
        * */
        return UnifyResultUtil.success(body);
    }

}
