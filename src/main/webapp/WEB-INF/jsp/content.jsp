<%--
Created by IntelliJ IDEA.
User: Acer
Date: 28.09.2025
Time: 14:36
To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
    <%@ include file="header.jsp"%>
    <div>
        <span>Content. Русский язык</span>
        <p>Size: ${requestScope.users.size()}</p>
        <p>User: ${requestScope.users.get(0).id}</p>
        <p>User2: ${requestScope.users[1].id}</p>
        <p>Map: ${sessionScope.usersMap[1]}</p>
        <p>JSessionID: ${cookie["JSESSIONID"]}, unique identifier</p>
        <p>Header: ${header["Cookie"]}</p>
        <p>Param id: ${param.id}</p>
        <p>Param test: ${param.test}</p>
        <p>Empty list: ${not empty users}</p>
    </div>
    <%@ include file="footer.jsp"%>
</body>
</html>
