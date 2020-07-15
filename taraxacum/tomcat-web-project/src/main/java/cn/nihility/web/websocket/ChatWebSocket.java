package cn.nihility.web.websocket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author dandelion
 * @date 2020:06:27 13:37
 */
@ServerEndpoint(value = "/servlet31/websocket/chat", configurator = GetHttpSessionConfigurator.class)
public class ChatWebSocket {

    // 没次 webSocket 都会新创建一个 Socket
    private Session session;
    private HttpSession httpSession;

    // 当前在线用户数量
    private static AtomicInteger onlineUserCnt = new AtomicInteger(0);
    // 保存登录信息
    private final static Map<HttpSession, ChatWebSocket> onlineUsers = new HashMap<>();

    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        System.out.println("====================== ChatWebSocket onOpen ======================");
        System.out.println("ChatWebSocket instance : " + this);

        this.session = session;
        System.out.println("socket session id " + session);

        HttpSession httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
        this.httpSession = httpSession;
        System.out.println("HttpSession id " + httpSession.getId());

        if (httpSession.getAttribute("userName") != null) {
            incrementUserCnt();
            System.out.println("添加用户 : " + httpSession.getAttribute("userName"));
            onlineUsers.put(httpSession, this);
        } else {
            System.out.println("用户未登录");
        }

        // 广播消息
        String responseMessage = getContent("user", "", "", getOnlineUserNames());
        broadcastMessage(responseMessage);

        System.out.println("当前登录用户人数: " + getOnlineCnt());
        System.out.println("====================== ChatWebSocket onOpen ======================");
    }

    private void broadcastMessage(String responseMessage) {
        onlineUsers.values().forEach(s -> {
            try {
                s.session.getBasicRemote().sendText(responseMessage);
            } catch (IOException e) {
                System.out.println("broadcast message error" + e.getMessage());
            }
        });
    }

    /* {"fromName":"user","toName":"luck","content":"asdasda"} */
    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("====================== ChatWebSocket onMessage ======================");
        System.out.println("name : " + httpSession.getAttribute("userName") + " , message : " + message);

        JSONObject messageMap = JSONObject.parseObject(message);
        String fromName = messageMap.getString("fromName");
        String toName = messageMap.getString("toName");
        String content = messageMap.getString("content");

        if (toName == null || "".equals(toName.trim())) {
            return;
        }

        String sendMessage = getContent("message", fromName, toName, content);
        if ("all".equals(toName)) {
            broadcastMessage(sendMessage);
        } else {
            toUserMessage(sendMessage, fromName, toName);
        }

        System.out.println("====================== ChatWebSocket onMessage ======================");
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        System.out.println("====================== ChatWebSocket onClose ======================");
        System.out.println("close session " + session);
        System.out.println("关闭原因: " + closeReason);

        final Set<Map.Entry<HttpSession, ChatWebSocket>> entries = onlineUsers.entrySet();
        entries.removeIf(next -> next.getValue().session == session);

        System.out.println(onlineUsers.size());

        final Integer userCnt = decrementUserCnt();
        System.out.println("当前登录人数： " + userCnt);

        System.out.println("====================== ChatWebSocket onClose ======================");
    }

    @OnError
    public void OnError(Session session, Throwable thr) {
        System.out.println("====================== ChatWebSocket OnError ======================");
        System.out.println("error session " + session);

        thr.printStackTrace();
        System.out.println("服务异常");

        final Integer userCnt = decrementUserCnt();
        System.out.println("当前登录人数： " + userCnt);

        System.out.println("====================== ChatWebSocket OnError ======================");
    }

    private void toUserMessage(String message, String fromName, String toName) {
        boolean online = false;

        for (HttpSession httpSession : onlineUsers.keySet()) {
            if (toName.equals(httpSession.getAttribute("userName"))) {
                online = true;
            }
        }

        if (online) {
            onlineUsers.keySet().forEach(k -> {
                String u = (String) k.getAttribute("userName");
                if (toName.equals(u)) {
                    try {
                        onlineUsers.get(k).session.getBasicRemote().sendText(message);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private String getContent(String type, String fromName, String toName, String content) {
        Map<String, Object> map = new HashMap<>();
        map.put("type", type);
        map.put("fromName", fromName);
        map.put("toName", toName);
        map.put("data", content);
        return JSON.toJSONString(map);
    }

    private String getOnlineUserNames() {
        StringBuilder sb = new StringBuilder();
        onlineUsers.keySet().forEach(k -> sb.append(k.getAttribute("userName") + ","));
        return sb.toString();
    }

    private Integer getOnlineCnt() {
        return onlineUserCnt.get();
    }

    private Integer incrementUserCnt() {
        return onlineUserCnt.incrementAndGet();
    }

    private Integer decrementUserCnt() {
        return onlineUserCnt.decrementAndGet();
    }

}
