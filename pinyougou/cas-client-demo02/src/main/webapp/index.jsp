<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2018/9/22
  Time: 19:13
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>第一个cas应用</title>
</head>
<body>
    欢迎<%=request.getRemoteUser()%>使用CAS!port= 28085 <br/>
    <a href="http://localhost:28083/cas/logout?service=http://www.itheima.com">退出</a>
</body>
</html>
