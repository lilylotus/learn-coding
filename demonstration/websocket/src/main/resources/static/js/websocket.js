// jquery 文档准备完成后才执行写法一
// $(document).ready(function () {});

function stringIsBlank(str) {
    return typeof str == "undefined" || str == null || str === "";
}

function stringIsNotBlank(str) {
    return !stringIsBlank(str);
}

function objIsNull(obj) {
    return typeof obj == "undefined" || obj == null;
}

function objIsNotNull(obj) {
    return !objIsNull(obj);
}

/*
  1.通过ws模块来创建服务器
  2.服务器连接客户端---(给客户端编号)
  3.接收客户端发来的信息
  4.监听客户端下线
*/

function webSocketIsNotClosed(ws) {
    return objIsNotNull(ws) && ws.readyState !== 3;
}

function webSocketIsClosed(ws) {
    return !webSocketIsNotClosed(ws);
}

function webSocketClose(ws) {
    if (webSocketIsNotClosed(ws)) {
        console.log("------ close ------");
        ws.close();
    }
    $("#newUserListId").empty();
}

function buildErrorMsg(msg) {
    return '<span style="color: red;">' + msg + '</span>';
}

function buildTipMsg(msg) {
    return '<span style="color: green;">' + msg + '</span>';
}

function buildSuccessMsg(msg) {
    return '<span style="color: blue;">' + msg + '</span>';
}

function writeScreenMsg(msg) {
    $("#wsMsgFlow").append('<div>' + msg + '</div>');
}

function webSocketSendMsg(socket, msg) {
    if (webSocketIsNotClosed(socket)) {
        socket.send(msg);
    } else {
        $("#newUserListId").empty();
        writeScreenMsg(buildErrorMsg('WebSocket 已关闭，请重新连接！'));
    }
}

function userLoginIn(user) {
    if (objIsNull($('#' + user)[0])) {
        console.log('添加用户 [' + user + "]");
        // <div id="all1" class="radio"><label><input type="radio" name="option" value="all1" checked>所有用户1</label></div>
        $("#newUserListId").append('<div id="' + user + '" class="radio"><label><input type="radio" name="option" value="' + user + '">' + user + '</label></div>');
    }
}

function userLoginOut(user) {
    // <div id="all1" class="radio"><label><input type="radio" name="option" value="all1" checked>所有用户1</label></div>
    var item = $('#' + user);
    if (objIsNotNull(item[0])) {
        item.remove();
    }
}

var globalWebSocket = null;

// 写法二
$(function () {

    // 请求连接
    $("#socketConnectId").click(function () {
        const addr = $("#wsAddr").val();
        if (stringIsBlank(addr)) {
            alert('WebSocket 连接地址不可为空！');
        } else if (webSocketIsNotClosed(globalWebSocket)) {
            alert('请先关闭原有 WebSocket 连接！');
        } else {
            console.log("连接到 WebSocket 服务器 [" + addr + "]");
            globalWebSocket = new WebSocket(addr);

            globalWebSocket.onopen = function (event) {
                console.log("连接到 WebSocket 服务器成功！");
                writeScreenMsg(buildTipMsg('连接到 WebSocket 服务器成功！'));
            };

            globalWebSocket.onclose = function (event) {
                console.log("关闭到 WebSocket 服务器连接！");
                webSocketClose(globalWebSocket);
                writeScreenMsg(buildErrorMsg('关闭到 WebSocket 服务器连接！'));
                $("#newUserListId").empty();
            };

            globalWebSocket.onmessage = function (event) {
                console.log("收到消息 [" + event.data + "]");
                var dt = event.data;
                var user;
                if (dt.startsWith('用户进入:')) {
                    user = dt.replace('用户进入:', '');
                    console.log("新用户 [" + user + "] 进入");
                    user.split(',').forEach(u => userLoginIn(u));
                } else if (dt.startsWith('用户退出:')) {
                    user = dt.replace('用户退出:', '');
                    console.log("用户 [" + user + "] 退出");
                    userLoginOut(user);
                }
                writeScreenMsg(buildSuccessMsg("收到消息 [" + event.data + "]"));
            };

            globalWebSocket.onerror = function (event) {
                console.log("异常 ", event);
                writeScreenMsg(buildErrorMsg('异常' + event));
            };

            // 监听可能发生的错误
            /*globalWebSocket.addEventListener('error', function (event) {
                console.log('WebSocket error: ', event);
            });*/

        }
    });

    $("#socketCloseId").click(function () {
        webSocketClose(globalWebSocket);
        globalWebSocket = null;
    });

    $("#socketSendId").click(function () {
        const msg = $("#wsSendMsgId").val();
        const usr = $("#wsUserListId input[name='option']:checked").val();
        console.log("选择的用户 [" + usr + "]");
        if (stringIsNotBlank(msg)) {
            webSocketSendMsg(globalWebSocket, usr + ":" + msg);
        }
    });

});
