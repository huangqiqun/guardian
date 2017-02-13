<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.leadbank.guardian.client.util.AbstractGuardianFilter" %>
<%@ page import="com.leadbank.guardian.client.validation.Assertion" %>
<%@ page import="com.leadbank.common.context.ApplicationContextConfig" %>
<%@ page import="com.leadbank.guardian.client.authentication.AttributePrincipal" %>
<html>
<head>
    <script src="static/jquery/jquery-1.8.3.js" type="text/javascript"></script>
    <script src="static/jquery/jquery.cookie.js" type="text/javascript"></script>
	<script type="application/javascript">
		function logout() {
			var guardianTGC = $.cookie('GuardianTGC');
			if (guardianTGC) {
				$.ajax({
					url: '<%=ApplicationContextConfig.get("guardianServerUrlPrefix")%>/logout?ticketGrantingTicketId=' + $.cookie('GuardianTGC'),
					dataType: "jsonp",
					jsonp: "callback",
					success: function (data) {
						if (data) {
							$.each(data.clients, function (i, e) {
								$.ajax({
									async: false,
									url: e + '/cookie?method=remove',
									dataType: 'jsonp',
									jsonp: 'callback'
								});
							});

							var url = '<%=request.getRequestURI()%>';
							if (window.location) {
								window.location.reload(url);
							} else {
								document.location.reload(url);
							}
						}
					}
				});
			} else {
				alert('登出失败!');
			}
		}
	</script>
</head>
<body>
<div>
	<h5>会话ID：</h5>
	<%=session.getAttribute("sessionId")%>
</div>
<%AttributePrincipal principal = ((Assertion) session.getAttribute(AbstractGuardianFilter.CONST_GUARDIAN_ASSERTION)).getPrincipal();%>
<div>
	<h5>用户ID：</h5>
	<%=principal.getName()%>
</div>
<div>
	<h5>属性：</h5>
	<%
		Map<String, Object> attributes = principal.getAttributes();
		for (Map.Entry<String, Object> attribute : attributes.entrySet()) {
			out.println(attribute.getKey() + "->" + attribute.getValue() + "<br>");
		}
	%>
</div>
<a href="javascript:logout();" style="display: inline-block; cursor: pointer">登出</a>
</body>
</html>
