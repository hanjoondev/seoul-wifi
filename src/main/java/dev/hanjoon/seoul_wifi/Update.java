package dev.hanjoon.seoul_wifi;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import javax.naming.NamingException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

@WebServlet(name = "Update", value = "/update")
public class Update extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HotspotDao hotspotDao = new HotspotDao();
        int updated = 0;
        long start = System.nanoTime();
        try {
            updated = hotspotDao.update();
        } catch (NamingException | SQLException e) {
            e.printStackTrace();
        }
        long end = System.nanoTime();
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.printf("Took %,.0fms to update %,d entries on the database", (end - start) / 1e6, updated);
        out.printf("<br /><a href='https://hanjoon.dev/seoul-wifi/'>Return to Home</a>");
    }
}
