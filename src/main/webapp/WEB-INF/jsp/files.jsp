<%--
  Created by IntelliJ IDEA.
  User: Acer
  Date: 05.10.2025
  Time: 14:57
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>File upload</title>
</head>
<body>
<form action="/files" method="post" enctype="multipart/form-data">
    <label for="user_id">UserId:
        <input type="text" name="user_id" id="user_id" required>
    </label><br>
    <label for="file">Select File:
        <input type="file" name="file" id="file" required>
    </label><br>
    <button type="submit">Upload file</button>
</form>
</body>
</html>
