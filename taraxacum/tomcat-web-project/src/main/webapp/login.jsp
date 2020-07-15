<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <meta http-equiv="content-type" content="text/html; charset=utf-8">
    <title>web socket login</title>
    <link rel="stylesheet" type="text/css" href="css/login.css">
    <script rel="script" type="text/javascript" src="js/jquery-3.5.0.min.js"></script>
    <script rel="script" type="text/javascript" src="js/login.js"></script>
</head>
<body>

<div>
    <div>
        <h2>登录</h2>
        <form>
            <div>
                <label for="userName">用户名</label><input type="text" id="userName"/>
            </div>

            <div>
                <label for="password">密码</label><input type="text" id="password"/>
            </div>

            <div class="clear"></div>
            <input type="button" value="登录" onclick="login()"/>
        </form>
    </div>
</div>

</body>
</html>
