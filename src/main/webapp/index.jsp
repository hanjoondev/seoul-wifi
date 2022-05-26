<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="dev.hanjoon.seoul_wifi.Main" %>
<html>
<head>
    <title>Seoul Public WiFi Lookup</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/meyer-reset/2.0/reset.min.css">
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
<h1>Seoul Public WiFi Hotspot Finder</h1>
<section>
    <div class="container">
        <form action="<%= request.getContextPath()%>/nearest.jsp" method="post">
            Latitude: <input type="text" id="lat" name="lat"> Longitude: <input type="text" id="lnt" name="lnt">
            <input type="submit" value="Find the nearest 20 public WiFi hotspots"> </form>
        <form action="<%= request.getContextPath()%>/history.jsp" method="post"> <input type=submit id="history" value="Last 20 Search History"> </form>
        <form action="<%= request.getContextPath()%>/sqlite.jsp" method="post"> <input type=submit id="sqlite" value="Update SQLite"> </form>
    </div>
</section>
<section>
<%
    Main main = new Main();
    out.print(main.getBaseTable());
%>
</section>
</body>
</html>
