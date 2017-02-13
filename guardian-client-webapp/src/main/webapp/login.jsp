<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@page import="com.leadbank.common.context.ApplicationContextConfig"%>
<html>
<head>
    <script src="static/jquery/jquery-1.8.3.js" type="text/javascript"></script>
    <script src="static/jquery/jquery.cookie.js" type="text/javascript"></script>
    <script type="text/javascript">
	   	$(function() {
            //表单提交
            $('#loginForm').submit(function () {
                $('#error').text('');
                $.ajax({
                    url: '<%=ApplicationContextConfig.get("guardianServerLoginUrl")%>',
                    data: $(this).serializeArray(),
                    dataType: 'jsonp',
                    jsonp: 'callback',
                    success: function (data) {
                        if (data) {
                            if (data.status == 'S') {
                                $.each(data.clients, function (i, e) {
                                    $.ajax({
                                        async: false,
                                        url: e + '/cookie?method=add',
                                        data: {'guardianTGC': data.ticketGrantingTicketId},
                                        dataType: 'jsonp',
                                        jsonp: 'callback'
                                    });
                                });

                                var url = '<%=request.getParameter("originalUrl")%>';
                                url += (url.indexOf('?') == -1 ?  '?': '&') + 'ticket=' + data.serviceTicketId;
                                if (window.location) {
                                    window.location.replace(url);
                                } else {
                                    document.location.replace(url);
                                }
                            } else if (data.status == 'F') {
                                $('#error').text(data.respCode + '->' + data.respMessage);
                            }
                        }
                    }
                });

                return false;
            });
    	});
    </script>
</head>
<body>
<h2>登录</h2>
<div id="error"></div>
<form id="loginForm" method="post" action="">
    <input type="hidden" id="sessionName" name="sessionName" value="<%=request.getAttribute("sessionName")%>">
    <input type="hidden" id="service" name="service" value="<%=ApplicationContextConfig.get("serverName")%>">
    <input type="text" id="username" name="username" value="18016023813">
    <input type="text" id="password" name="password" value="chejing88">
    <input type="submit" id="submit" value="提交">
    <input type="reset" id="reset" value="取消">
</form>
</body>
</html>