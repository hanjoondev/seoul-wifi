<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<html>
<head>
    <title>Seoul Public WiFi Hotspot Finder</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/meyer-reset/2.0/reset.min.css">
    <link rel="stylesheet" type="text/css"  href="<c:url value='/css/style.css' />">
</head>
<body>
<h1>Seoul Public WiFi Hotspot Finder</h1>
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
                    <th>Distance</th>
                    <th>District</th>
                    <th>Address</th>
                    <th>Detail</th>
                    <th>Type</th>
                    <th>Latitude</th>
                    <th>Longitude</th>
                </tr>
            </thead>
        </table>
    </div>
    <div class="tbl-content">
        <table>
            <tbody>
                <tr>
                </tr>
            </tbody>
        </table>
    </div>
</section>
</body>
</html>
