package cn.nihility.spring.security.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author orchid
 * @date 2021-05-11 00:24:57
 */
public class SecurityAccessDeniedHandler implements AccessDeniedHandler {

    private static final Logger log = LoggerFactory.getLogger(SecurityAccessDeniedHandler.class);

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {
        log.error("访问 [{}] 受限 [{}]", request.getRequestURI(), accessDeniedException.getMessage());

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=utf-8");

        PrintWriter writer = response.getWriter();
        writer.write("{\"code\": 403, \"message\": \"访问 [" +
            request.getRequestURI() + "] 受限 [" + accessDeniedException.getMessage() + "]\"}");
        writer.flush();

    }

}
