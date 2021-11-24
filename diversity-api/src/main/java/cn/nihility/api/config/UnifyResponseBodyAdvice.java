package cn.nihility.api.config;

import cn.nihility.api.annotation.UnifyResponseResult;
import cn.nihility.common.pojo.UnifyResult;
import cn.nihility.common.util.JacksonUtil;
import cn.nihility.common.util.UnifyResultUtil;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.stream.Stream;

/**
 * 统一封装返回体 (response body)
 */
@RestControllerAdvice
public class UnifyResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    private static final Class<?>[] RESPONSE_TYPES = {String.class, Integer.class, Collection.class};

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        if (AnnotatedElementUtils.hasAnnotation(returnType.getContainingClass(), UnifyResponseResult.class)) {
            return true;
        }
        Method executeMethod = returnType.getMethod();
        if (executeMethod != null) {
            if (executeMethod.getAnnotation(UnifyResponseResult.class) != null) {
                return true;
            } else {
                for (Class<?> clazz : RESPONSE_TYPES) {
                    if (clazz.isAssignableFrom(executeMethod.getReturnType()) ||
                        executeMethod.getReturnType().isArray()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {

        Method returnTypeMethod = returnType.getMethod();
        final Object resultBody = (body instanceof UnifyResult) ? body : UnifyResultUtil.success(body);

        if ((MediaType.TEXT_HTML.equals(selectedContentType) ||
            MediaType.TEXT_PLAIN.equals(selectedContentType))) {
            String result = JacksonUtil.toJsonString(resultBody);
            if (returnTypeMethod != null && Stream.of(returnTypeMethod.getAnnotations())
                .anyMatch(a -> (a instanceof RequestMapping) &&
                    ((RequestMapping) a).produces().length > 0)) {
                return result;
            }
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            return result;
        }

        return resultBody;
    }

}
