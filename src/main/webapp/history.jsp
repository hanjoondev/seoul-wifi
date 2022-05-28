<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Seoul Public WiFi Hotspot Finder - Query History</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/meyer-reset/2.0/reset.min.css">
    <link rel="stylesheet" type="text/css"  href="<c:url value='/css/style.css' />">
</head>
<body>
<h1>Seoul Public WiFi Hotspot Finder - Last 20 Queries</h1>
<section>
    <div class="container">
        <form action="<%= request.getContextPath()%>/find" method="get">
            Latitude: <input type="text" id="lat" name="lat" value="" />
            Longitude: <input type="text" id="lng" name="lng" value="" />
            <input type="submit" value="Find the nearest 20 public WiFi hotspots"> </form>
        <form action="<%= request.getContextPath()%>/history" method="get"> <input type="submit" id="history" value="Last 20 Search History"> </form>
        <form action="<%= request.getContextPath()%>/update" method="get"> <input type="submit" id="update" value="Update SQLite"> </form>
        <form action="<%= request.getContextPath()%>/" method="get"> <input type="submit" id="home" value="Return to Home"> </form>
    </div>
</section>
<section>
    <div class="tbl-header">
        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Latitude</th>
                    <th>Longitude</th>
                    <th>Time</th>
                    <th>Delete</th>
                </tr>
            </thead>
        </table>
    </div>
    <div class="tbl-content">
        <table>
            <tbody>
                <c:forEach var="history" items="${histories}">
                <tr>
                    <td>${history.getId()}</td>
                    <td>${history.getLat()}</td>
                    <td>${history.getLng()}</td>
                    <td>${history.getRequestedAt()}</td>
                    <td><form action="<%= request.getContextPath()%>/history" method="get">
                        <input type="hidden" name="delete" value="${history.getId()}">
                        <input type="submit" value="Delete"></form></td>
                </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>
</section>
</body>
</html>
