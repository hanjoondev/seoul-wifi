package dev.hanjoon.seoulwifi;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.naming.NamingException;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Servlet implementation class for the history table
 */
@WebServlet(name = "GetHistory", value = "/history")
public class GetHistory extends HttpServlet {
    private void process(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException, NamingException {
        String id = request.getParameter("delete");
        try {
            HotspotDao hotspotDao = new HotspotDao();
            hotspotDao.delete(Integer.parseInt(id));
        } catch (NumberFormatException e) { }
        HotspotDao hotspotDao = new HotspotDao();
        Hotspot[] histories = hotspotDao.history();
        request.setAttribute("histories", histories);
        RequestDispatcher rd = request.getRequestDispatcher("history.jsp");
        rd.forward(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            process(request, response);
        } catch (SQLException | NamingException e) {
            e.printStackTrace();
        }
    }
}
