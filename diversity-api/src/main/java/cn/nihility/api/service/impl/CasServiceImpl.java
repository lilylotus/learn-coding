package cn.nihility.api.service.impl;

import cn.nihility.api.dto.CasResponse;
import cn.nihility.api.properties.AuthenticationProperties;
import cn.nihility.api.service.ICasService;
import cn.nihility.api.service.ISessionService;
import cn.nihility.api.service.ITokenService;
import cn.nihility.api.util.CookieUtils;
import cn.nihility.common.constant.AuthConstant;
import cn.nihility.common.constant.CasConstant;
import cn.nihility.common.constant.RequestMethodEnum;
import cn.nihility.common.entity.AuthenticateSession;
import cn.nihility.common.entity.AuthenticationToken;
import cn.nihility.common.exception.AuthenticationException;
import cn.nihility.common.http.RequestContextHolder;
import cn.nihility.common.pojo.ResponseHolder;
import cn.nihility.common.util.AuthenticationUtils;
import cn.nihility.common.util.HttpClientUtils;
import cn.nihility.common.util.HttpRequestUtils;
import cn.nihility.common.util.JacksonUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

/**
 * @author nihility
 * @date 2022/02/21 15:53
 */
@Service
public class CasServiceImpl implements ICasService {

    private static final Logger log = LoggerFactory.getLogger(CasServiceImpl.class);

    private ITokenService tokenService;
    private ISessionService sessionService;
    private AuthenticationProperties properties;

    public CasServiceImpl(ITokenService tokenService, ISessionService sessionService,
                          AuthenticationProperties properties) {
        this.tokenService = tokenService;
        this.sessionService = sessionService;
        this.properties = properties;
    }

    @Override
    public String casLogin(HttpServletRequest request, HttpServletResponse response) {

        String service = request.getParameter(CasConstant.PARAM_SERVICE);
        String domain = HttpRequestUtils.getOriginRequestUrl(request);
        log.info("CAS login service url [{}] domain [{}]", service, domain);
        if (StringUtils.isBlank(service)) {
            service = domain + "/" + CasConstant.DEFAULT_SUCCESS_PAGE;
        }

        String redirectUrl = domain + "/auth/cas/login?service=" + HttpRequestUtils.urlEncode(service);
        String frontRedirectUrl = "/auth/login?redirect=" + HttpRequestUtils.urlEncode(redirectUrl);
        log.info("CAS redirectUrl [{}]", redirectUrl);
        log.info("CAS frontRedirectUrl [{}]", frontRedirectUrl);

        AuthenticateSession session = RequestContextHolder.getContext().getAuthSession();

        if (null != session) {
            // 删除原有的 token
            Optional.ofNullable(session.getTokenSet())
                .orElse(Collections.emptySet())
                .forEach(t -> tokenService.deleteCasToken(t.getTokenId()));
            // 生成服务票据 ST
            AuthenticationToken token = AuthenticationUtils.createToken(session.getSessionId(), CasConstant.PROTOCOL,
                CasConstant.SERVICE_TICKET, CasConstant.SERVICE_TICKET);
            AuthenticationToken tgtToken = AuthenticationUtils.createToken(session.getSessionId(), CasConstant.PROTOCOL,
                CasConstant.SERVICE_TICKET, CasConstant.TGT_TICKET);
            token.setRefTokenId(tgtToken.getTokenId());

            tokenService.createCasToken(tgtToken);
            tokenService.createCasToken(token);

            session.addToken(tgtToken);
            session.addToken(token);
            sessionService.updateSession(session);

            CookieUtils.setCookie(CasConstant.COOKIE_CASTIC, token.getTokenId(), AuthConstant.TOKEN_TTL, response);

            String stRedirectUrl = HttpRequestUtils.addUrlParam(HttpRequestUtils.urlParamsEncode(service),
                CasConstant.PARAM_TICKET, token.getTokenId());
            log.info("CAS st url [{}]", stRedirectUrl);
            return stRedirectUrl;

        }

        return frontRedirectUrl;
    }

    @Override
    public CasResponse serviceValidate(HttpServletRequest request, HttpServletResponse response) {

        String ticket = request.getParameter(CasConstant.PARAM_TICKET);
        String service = request.getParameter(CasConstant.PARAM_SERVICE);
        log.info("CAS service validate ticket [{}], service [{}]", ticket, service);

        if (StringUtils.isBlank(ticket)) {
            throw new AuthenticationException("参数 ticket 不可为空");
        }
        if (StringUtils.isBlank(service)) {
            throw new AuthenticationException("参数 service 不可为空");
        }

        AuthenticationToken token = tokenService.getCasToken(ticket);
        if (null == token) {
            throw new AuthenticationException("票据不存在或失效");
        } else {
            tokenService.deleteCasToken(ticket);
        }
        String sessionId = token.getSessionId();
        AuthenticateSession session = sessionService.getSessionById(sessionId, response);
        if (null == session) {
            throw new AuthenticationException("认证会话过期");
        }

        CasResponse casResponse = new CasResponse();
        casResponse.setUser(session.getUserId());
        casResponse.setAttributes(session.getUserAttributes());

        return casResponse;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> rec(HttpServletRequest request, HttpServletResponse response) {

        String ticket = request.getParameter(CasConstant.PARAM_TICKET);
        log.info("CAS receive ticket [{}]", ticket);
        if (StringUtils.isBlank(ticket)) {
            throw new AuthenticationException("参数 ticket 不可为空");
        }

        String domain = HttpRequestUtils.getOriginRequestUrl(request);
        String redirectUrl = properties.getSsoHost() + "/auth/cas/serviceValidate?service=" +
            HttpRequestUtils.urlEncode(domain) + "&ticket=" + ticket;
        log.info("CAS receive domain [{}] redirectUrl [{}]", domain, redirectUrl);

        ResponseHolder<Map> holder = HttpClientUtils.executeFormWithResponse(redirectUrl,
            RequestMethodEnum.GET, null, Map.class);

        log.info("CAS service validate response [{}]", holder);

        if (HttpStatus.OK.value() == holder.getStatusCode()) {
            return holder.getContent();
        } else {
            try {
                return JacksonUtils.toMap(holder.getErrorContent());
            } catch (JsonProcessingException e) {
                log.error("解析响应出错 {}", holder.getErrorContent(), e);
                return Collections.singletonMap("message", "内部异常");
            }
        }

    }

}
