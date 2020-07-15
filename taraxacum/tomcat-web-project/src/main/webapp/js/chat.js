
var ws = null;

function startWebSocket(userName) {

    // 构建 websocket 对象
    if ('WebSocket' in window) {
        ws = new WebSocket('ws://localhost:8080/servlet31/websocket/chat');
    } else if ('MozWebSocket' in window) {
        ws = new MozWebSocket('ws://localhost:8080/servlet31/websocket/chat');
    } else {
        alert("not support");
    }

    console.log("websocket username : " + userName);

    /* {"data":"user,luck,","toName":"","fromName":"","type":"user"} */
    var setMessageInnerHtml = function (obj, userName) {
        if ('all' === obj.toName) {
            $('#broadcast-msg').append('<li>' + obj.fromName + ' 群发 : ' + obj.data + '</li>');
        } else {
            $('#msg-list').append('<li>' + obj.fromName + ' : ' + obj.data + '</li>');
        }

    };

    ws.onopen = function(event) {
        console.log("web socket is open now.")
    };

    ws.onclose = function(event) {
        console.log("web socket is closed now.")
    };

    // 监听信息
    ws.onmessage = function (evt) {
        var _data = evt.data;
        console.log('>> : ' + _data);

        var obj = JSON.parse(_data);

        if (obj.type === 'message') {
            setMessageInnerHtml(obj, userName);

        } else if (obj.type === 'user') {
            var ua = obj.data.split(',');
            $('#friends-list').empty();
            $('#friends-list').append('<li><input type="radio" name="toUser" value="all"/>广播</li>');

            $.each(ua, function (n, value) {
                if (value === userName || value === '') {
                    console.log('valid u ' + value);
                } else {
                    $('#friends-list').append('<li><input type="radio" name="toUser" value="' + value + '"/>' + value + '</li>');
                    $('#broadcast-msg').append('<li>您的好友 ' + value + ' 已上线</li>');
                }
            });

        }
    };

}

$(document).ready(function () {
    $('#socketClose').click(function () {
        console.log("web socket close");
        if (ws != null) {
            // ws.close(1000, '客户端关闭连接');
            ws.close();
            ws = null;
        } else {
            console.log("socket 已经关闭");
        }
    });
});

function sendMessage(userName) {

    var content = $('#send-msg-content').val();

    if (!content) {
        alert('请输入内容');
        return ;
    }

    var  message = {};
    message.fromName = userName;
    message.toName = $(':input:radio:checked').val();
    message.content = content;

    var msg = JSON.stringify(message);
    console.log('send msg : ' + msg);

    ws.send(msg);

    $('#send-msg-content').empty();

    $('#msg-list').append('<li>' + content + '</li>');
}