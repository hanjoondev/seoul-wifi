<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="dev.hanjoon.seoul_wifi.Main" %>

<!DOCTYPE html public>
<html>
<head>
    <title>Seoul Public WiFi Lookup - SQLite Update Result</title>
</head>
<body>
    <%
        Main main = new Main();
        String[] result = main.updateSqlite();
        out.print(String.format("Took %sms to update SQLite DB<br />", result[0]));
        out.print(String.format("Number of entries: %s<br />", result[1]));
        out.print(String.format("Number of duplicates: %s<br />", result[2]));
        out.print(String.format("Updated: %s<br />", result[3]));
    %>
</body>
</html>
