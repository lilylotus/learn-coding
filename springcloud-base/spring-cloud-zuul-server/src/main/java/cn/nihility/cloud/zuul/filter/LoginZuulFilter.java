package cn.nihility.cloud.zuul.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * LoginZuulFilter
 *
 * @author dandelion
 * @date 2020-04-27 10:21
 */
@Component
public class LoginZuulFilter extends ZuulFilter {
    private static final Logger log = LoggerFactory.getLogger(LoginZuulFilter.class);

    /* 定义过滤器类型， pre|routing|post|error */
    @Override
    public String filterType() {
        return "pre";
    }

    /* 过滤器的执行顺序，返回值越小，执行顺序越高 */
    @Override
    public int filterOrder() {
        return 0;
    }

    /* 过滤器是否生效，true：使用此过滤器 */
    @Override
    public boolean shouldFilter() {
        return true;
    }

    /* 指定过滤器中的业务逻辑 */
    /* 身份认证 header 必须含有 token， 通过 RequestContext 获取上下文对象 */
    @Override
    public Object run() throws ZuulException {
        log.info("执行 LoginZuulFilter -> run()");
        // 1. 获取 zuul 提供的 RequestContext 对象
        final RequestContext currentContext = RequestContext.getCurrentContext();
        // 2. 从 RequestContext 对象获取 Request 对象
        final HttpServletRequest request = currentContext.getRequest();
        // 3. 获取 access-token header
        final String token = request.getParameter("access-token");
        // 4. 校验 header
        if (StringUtils.isEmpty(token)) {
            // 4.1 拦截请求，校验认证失败
            currentContext.setSendZuulResponse(false);
            currentContext.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
        }
        return null;
    }
}
