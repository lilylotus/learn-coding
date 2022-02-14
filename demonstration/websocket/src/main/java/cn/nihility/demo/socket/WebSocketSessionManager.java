package cn.nihility.demo.socket;

import cn.nihility.demo.constant.Constant;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.CloseReason;
import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class WebSocketSessionManager {

    private static final Logger log = LoggerFactory.getLogger(WebSocketSessionManager.class);

    private static final Map<String, Session> SESSION = new ConcurrentHashMap<>();

    public boolean sessionIsClosed(Session session) {
        return null == session || !session.isOpen();
    }

    public void addSession(String id, Session session) {
        if (StringUtils.isBlank(id) || null == session) {
            return;
        }
        Session oldSession = SESSION.remove(id);
        if (oldSession != null) {
            sendMessage(oldSession, "发现您从别的客户端登录，当前会话被关闭！");
            closeSession(oldSession);
        }
        SESSION.put(id, session);
        List<Session> list = new ArrayList<>(SESSION.values());
        StringJoiner joiner = new StringJoiner(",");
        SESSION.keySet().forEach(joiner::add);
        String users = joiner.toString();
        for (Session s : list) {
            sendMessage(s, "用户进入:" + users);
        }
    }

    public void removeSession(String id) {
        if (StringUtils.isNotBlank(id)) {
            sendMsgToAllSession(id, "用户退出:" + id);
            SESSION.remove(id);
        }
    }

    public void closeSession(Session session) {
        if (!sessionIsClosed(session)) {
            log.info("服务器关闭 [{}] Socket 连接", parseRemoteAddress(session));
            try {
                session.close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "服务端关闭连接"));
            } catch (IOException e) {
                log.error("关闭 Session [{}] 异常", parseRemoteAddress(session), e);
            }
        }
    }

    public void sendMessage(Session session, String msg) {
        if (StringUtils.isBlank(msg) || null == session || !session.isOpen()) {
            return;
        }
        try {
            session.getBasicRemote().sendText(msg);
        } catch (IOException ex) {
            log.error("向 [{}] 发送消息 [{}] 异常", parseRemoteAddress(session), msg, ex);
        }
    }

    public void sendMsgToAllSession(String id, String msg) {
        if (SESSION.containsKey(id)) {
            Set<String> st = new HashSet<>(SESSION.keySet());
            st.remove(id);
            for (String s : st) {
                sendMsgToSpecifySession(s, msg);
            }
        }
    }

    public void sendMsgToSpecifySession(String id, String msg) {
        if (StringUtils.isBlank(id) || StringUtils.isBlank(msg)) {
            return;
        }
        sendMessage(SESSION.get(id), msg);
    }

    public void routeMessage(String id, String message) {
        if (StringUtils.isBlank(message)) {
            return;
        }

        String[] sp = message.split(Constant.MESSAGE_SPLIT_TAG);
        if (Constant.MESSAGE_SPLIT_LEN == sp.length) {
            String key = sp[0];
            String msg = sp[1];
            log.info("消息路由 key [{}], msg [{}]", key, msg);
            if (Constant.ALL_USER_KEY.equals(key)) {
                sendMsgToAllSession(id, id + " > " + msg);
            } else {
                sendMsgToSpecifySession(key, id + " > " + msg);
            }
        } else {
            sendMessage(SESSION.get(id), "消息格式不正确");
        }

    }


    public static String parseRemoteAddress(Session session) {
        if (session == null) {
            return null;
        }
        RemoteEndpoint.Async async = session.getAsyncRemote();
        //在Tomcat 8.0.x版本有效
        // InetSocketAddress addr = (InetSocketAddress) getFieldInstance(async,"base#sos#socketWrapper#socket#sc#remoteAddress");
        //在Tomcat 8.5以上版本有效
        return Objects.toString(getFieldInstance(async, "base#socketWrapper#socket#sc#remoteAddress"), "unknown");
    }

    private static Object getFieldInstance(Object obj, String fieldPath) {
        String[] fields = fieldPath.split("#");
        for (String field : fields) {
            obj = getField(obj, obj.getClass(), field);
            if (obj == null) {
                return null;
            }
        }
        return obj;
    }

    private static Object getField(Object obj, Class<?> clazz, String fieldName) {
        for (; clazz != Object.class; clazz = clazz.getSuperclass()) {
            try {
                Field field = clazz.getDeclaredField(fieldName);
                if (null != field) {
                    field.setAccessible(true);
                    return field.get(obj);
                }
            } catch (Exception ignored) {
            }
        }
        return null;
    }

}
