package cn.nihility.demo.socket;

import cn.nihility.demo.config.WebSocketEndpointConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

/**
 * {@link javax.websocket.Endpoint}
 * <p>
 * 通过 ws://ip:port/ws/{name} 访问
 */
@Component
@ServerEndpoint(value = "/ws/{name}", configurator = WebSocketEndpointConfiguration.class)
public class WebSocketEndpoint {

    private static final Logger log = LoggerFactory.getLogger(WebSocketEndpoint.class);
    private static final WebSocketSessionManager MANAGER = new WebSocketSessionManager();

    /**
     * 每一个 WebSocket 客户端连接上来，都会新创建一个对象实例，所有这个对象数据定义是安全的
     */
    private String id;

    @OnOpen
    public void onOpen(Session session, EndpointConfig config, @PathParam("name") String name) throws IOException {
        log.info("on open, session id [{}], path param [{}]", session.getId(), name);
        Object configValue = config.getUserProperties().get("timestamp");
        log.info("user property timestamp [{}]", configValue);
        sendMessage(session, "connection success.");
        log.info("客户端连接地址 [{}]", WebSocketSessionManager.parseRemoteAddress(session));
        this.id = name;
        if (StringUtils.isBlank(name)) {
            log.error("登录名不可为空，关闭连接");
            MANAGER.sendMessage(session, "登录名不可为空，关闭连接");
            MANAGER.closeSession(session);
        } else {
            MANAGER.addSession(id, session);
        }
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        log.info("客户端关闭连接 [{}], reason [{}]", session.getId(), closeReason);
        MANAGER.removeSession(id);
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        log.info("onMessage [{}]:[{}]", session.getId(), message);
        MANAGER.routeMessage(id, message);
    }

    @OnError
    public void onError(Session session, Throwable throwable) throws IOException {
        log.info("onError [{}], error message [{}]", session.getId(), throwable.getMessage());
        sendMessage(session, "encounter trouble:" + throwable.getMessage());
    }

    private void sendMessage(Session session, String message) throws IOException {
        if (session != null && session.isOpen()) {
            session.getBasicRemote().sendText("sever:" + message);
        }
    }

}
