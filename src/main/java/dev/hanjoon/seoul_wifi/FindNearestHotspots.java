package dev.hanjoon.seoul_wifi;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;


@WebServlet(name = "FindNearestHotspots", value = "/find")
public class FindNearestHotspots extends HttpServlet {
    private void process(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String lat = request.getParameter("lat"), lng = request.getParameter("lng");
        try {
            double latitude = Double.parseDouble(lat);
            double longitude = Double.parseDouble(lng);
            if (latitude < -90 || latitude > 90 || longitude < -180 || longitude > 180) {
                Hotspot[] hotspots = { new Hotspot(-1, "Input out of range", "Acceptable range",
                        "-90 <= LAT <= 90", "-180 <= LNG <= 180", latitude, longitude) };
                request.setAttribute("hotspots", hotspots);
                RequestDispatcher rd = request.getRequestDispatcher("find.jsp");
                rd.forward(request, response);
                return;
            }
        } catch (NumberFormatException e) {
            Hotspot[] hotspots = { new Hotspot(-1, "Input must be numbers", "", "", "", 0, 0) };
            request.setAttribute("hotspots", hotspots);
            RequestDispatcher rd = request.getRequestDispatcher("find.jsp");
            rd.forward(request, response);
        }
        HotspotDao hotspotDao = new HotspotDao();
        Hotspot[] hotspots = hotspotDao.find(Double.parseDouble(lat), Double.parseDouble(lng), 20);
        request.setAttribute("hotspots", hotspots);
        RequestDispatcher rd = request.getRequestDispatcher("find.jsp");
        rd.forward(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        process(request, response);
    }
}