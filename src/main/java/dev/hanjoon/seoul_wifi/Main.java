package dev.hanjoon.seoul_wifi;

import javax.naming.NamingException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Main {
    private static String firstEntry;
    private static int numEntries;

    private static final String[] headers = { "District", "Address", "Detail",
                                              "Type", "Latitude", "Longitude" };
    private static final String[] keys = { "X_SWIFI_WRDOFC", "X_SWIFI_ADRES1", "X_SWIFI_ADRES2",
                                           "X_SWIFI_INOUT_DOOR", "LNT", "LAT" };

    private static void setValues() throws IOException {
        if (firstEntry != null) return;
        firstEntry = JsonHandler.dl(1, 1);
        numEntries = JsonHandler.getCount(firstEntry);
    }

    public String[] updateSqlite() throws IOException, SQLException, NoSuchAlgorithmException, NamingException {
        setValues();
        long start = System.nanoTime();        
        int updated = SqliteHelper.insert(keys, numEntries);
        long end = System.nanoTime();
        return new String[] { String.format("%.2f", (double) (end - start) / 1e6),
                              String.valueOf(numEntries),
                              String.valueOf(numEntries - updated),
                              String.valueOf(updated) };
    }

    public String getBaseTable() throws NamingException, SQLException {
        Connection c = SqliteHelper.connect();
        if (c == null) return generateTable(headers,
                new String[][] {{ "failed to connect to the database", "", "", "", "", "" }});
        int limit = 100, row = 0;
        String[][] bodies = new String[limit][6];
        Statement s = c.createStatement();
        ResultSet rs = s.executeQuery("SELECT * FROM wifi LIMIT " + limit + ";");
        while (rs.next()) {
            for (int i = 0; i < keys.length; i++)
                bodies[row][i] = rs.getString(keys[i]);
            row++;
        }
        s.close();
        c.close();
        return generateTable(headers, bodies);
    }

    public String getNearestTable(String lat, String lnt) throws NamingException, SQLException {
        String[] heads = { "Distance", "District", "Address", "Detail", "Type", "Latitude", "Longitude" };
        try {
            double latitude = Double.parseDouble(lat);
            double longitude = Double.parseDouble(lnt);
            if (latitude < -90 || latitude > 90 || longitude < -180 || longitude > 180)
                return generateTable(heads, new String[][] {{ "Input out of range for latitude or longitude",
                                            "", "", "", "", "-90 <= LATITUDE <= 90", "-180 <= LONGITUDE <= 180" }});
        } catch (NumberFormatException e) {
            return generateTable(heads, new String[][] {{ "Input must be numbers", "", "", "", "", "", "" }});
        }
        int limit = 20, id = -1;
        String[][] bodies = new String[limit][7];
        double latitude = Double.parseDouble(lat), longitude = Double.parseDouble(lnt);
        Connection c = SqliteHelper.connect();
        if (c == null) return generateTable(headers,
                new String[][] {{ "failed to connect to the database", "", "", "", "", "" }});
        Statement s = c.createStatement();
        double range = findRange(s, latitude, longitude);
        String q = "SELECT X_SWIFI_WRDOFC, X_SWIFI_ADRES1, X_SWIFI_ADRES2, X_SWIFI_INOUT_DOOR, LAT, LNT FROM wifi\n"
                 + " WHERE lnt BETWEEN " + (latitude - range) + " AND " + (latitude + range)
                 + "   AND lat BETWEEN " + (longitude - range) + " AND " + (longitude + range);
        ResultSet rs = s.executeQuery(q);
        ArrayList<SqlData> candidates = new ArrayList<>();
        while (rs.next()) {
            SqlData cand = new SqlData(++id, rs.getString(keys[0]), rs.getString(keys[1]),
                    rs.getString(keys[2]), rs.getString(keys[3]),
                    rs.getDouble("LNT"), rs.getDouble("LAT"));
            cand.setDist(calculateDistance(latitude, longitude, cand.getLat(), cand.getLnt()));
            candidates.add(cand);
        }
        candidates.sort((x, y) -> (int) (x.getDist() * 10000 - y.getDist() * 10000));
        for (int i = 0; i < limit; i++)
            bodies[i] = candidates.get(i).getResults();
        s.executeUpdate("INSERT INTO history (lat, lnt, t) VALUES("
                            + latitude + ", " + longitude + ", datetime('now', 'localtime'));");
        s.close();
        c.close();
        return generateTable(heads, bodies);
    }

    public String getHistoryTable() throws NamingException, SQLException {
        String[] heads = { "ID", "Latitude", "Longitude", "Time", "Delete" };
        Connection c = SqliteHelper.connect();
        if (c == null) return generateTable(heads,
                new String[][] {{ "failed to connect to the database", "", "", "", "" }});
        int limit = 20, row = -1;
        String[][] bodies = new String[limit][5];
        String[] key = { "lat", "lnt", "t" };
        Statement s = c.createStatement();
        String q = "SELECT * FROM history\n"
                 + " ORDER BY id DESC\n"
                 + " LIMIT " + limit + ";";
        ResultSet rs = s.executeQuery(q);
        while (rs.next()) {
            bodies[++row][0] = rs.getString("id");
            for (int i = 0; i < key.length; i++)
                bodies[row][i + 1] = rs.getString(key[i]);
            bodies[row][4] = "<form action='delete.jsp' method='post'>"
                           + "<input type='hidden' name='row' value='" + bodies[row][0] +"'>"
                           + "<input type='submit' value='Delete'></form>";
        }
        s.close();
        c.close();
        return generateTable(heads, bodies);
    }

    public String deleteHistory(String id) throws NamingException, SQLException {
        String[] heads = { "ID", "Latitude", "Longitude", "Time", "Delete" };
        Connection c = SqliteHelper.connect();
        if (c == null) return generateTable(heads,
                new String[][] {{ "failed to connect to the database", "", "", "", "" }});
        Statement s = c.createStatement();
        s.executeUpdate("DELETE FROM history WHERE id = " + id + ";");
        s.close();
        c.close();
        return getHistoryTable();
    }

    private static double findRange(Statement s, double latitude, double longitude) throws SQLException {
        double low = 0, high = Math.max(Math.abs(37.566 - latitude), Math.abs(126.9784 - longitude)) + 1;
        int limiter = 0;
        while (low < high && limiter++ < 100) {
            double range = (high - low) / 2 + low;
            String q = "SELECT id, LAT, LNT FROM wifi\n"
                     + " WHERE lnt BETWEEN " + (latitude - range) + " AND " + (latitude + range)
                     + " AND lat BETWEEN " + (longitude - range) + " AND " + (longitude + range);
            ResultSet rs = s.executeQuery(q);
            int count = 0;
            while (rs.next()) {
                count++;
                if (count == 100) break;
            }
            if (count >= 20 && count < 100) return range;
            else if (count < 20) low = range + 0.0000000001;
            else high = range - 0.0000000001;
        }
        return high;
    }

    private static double calculateDistance(double lat1, double lnt1, double lat2, double lnt2) {
        double rad = 6378137.0;
        double dLat = Math.toRadians(lat2 - lat1), dLnt = Math.toRadians(lnt2 - lnt1);
        double a = Math.pow(Math.sin(dLat / 2), 2)
                 + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                 * Math.pow(Math.sin(dLnt / 2), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return rad * c / 1000;
    }

    private String generateTable(String[] head, String[][] body) {
        StringBuilder s = new StringBuilder();
        s.append("<div class=\"tbl-header\"><table cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n");
        s.append("  <thead>\n").append("    <tr>\n");
        for (String h : head)
            s.append("      <th>").append(h).append("</th>\n");
        s.append("    </tr>\n").append("  </thead>\n").append("</table></div>\n");
        s.append("<div class=\"tbl-content\"><table cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n").append("  <tbody>\n");
        for (String[] row : body) {
            s.append("    <tr>\n");
            for (String b : row)
                s.append("<td>").append(b).append("</td>\n");
            s.append("    </tr>\n");
        }
        return s.append("  </tbody>\n").append("</table></div>\n").toString();
    }

    public static void main(String[] args) {
        // new Main().updateMySql();
    }
}
