function login() {
    console.log("点击登录按钮")

    var name = $("#userName").val();
    var password = $("#password").val();

    console.log(name + ":" + password);

    $.ajax({
        url: "/servlet31/login",
        type: "POST",
        dataType: 'json',
        data: {
          userName: name,
          password: password
        },
        contentType: "application/x-www-form-urlencoded",
        success: function (data, status) {
            console.log(data.message + ' : ' + data.success + ' : ' + status);

            if (data.success) {
                window.location.href = "chat.jsp";
            } else {
                alert(data.message);
            }

        }

    });

}