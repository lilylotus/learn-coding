<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <title>web socket chat</title>
    <link rel="stylesheet" type="text/css" href="css/chat.css">
    <script rel="script" type="text/javascript" src="js/jquery-3.5.0.min.js"></script>
    <script rel="script" type="text/javascript" src="js/chat.js"></script>
    <script type="text/javascript">
        <%
            String name = (String) session.getAttribute("userName");
        %>
        var un = '<%= name%>';
        console.log('userName : ' + un)
    </script>
</head>
<body onload="startWebSocket(un);">
userName = <%=session.getAttribute("userName")%>
<hr/>

<div>
    <h5>消息</h5>
    <form>
        <div><ul id="msg-list"></ul></div>
    </form>
</div>
<hr>

<div>
    <h5>发送消息</h5>
    <form>
        <div><label for="send-msg-content"></label><input type="text" id="send-msg-content"/></div>
        <input type="button" value="发送" id="send-msg" onclick="sendMessage(un)">
    </form>
</div>
<hr>

<div>
    <h5>好友列表</h5>
    <form>
        <div>
            <ul id="friends-list"></ul>
        </div>
    </form>
</div>
<hr>

<div>
    <h5>广播</h5>
    <form>
        <div>
            <ul id="broadcast-msg"></ul>
        </div>
    </form>
</div>

<div>
    <h5 id="socketClose">关闭</h5>
</div>
</body>
</html>
