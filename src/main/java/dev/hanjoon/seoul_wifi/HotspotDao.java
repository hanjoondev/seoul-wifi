package dev.hanjoon.seoul_wifi;

import javax.naming.NamingException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class HotspotDao {
    protected int update() throws SQLException, NamingException, IOException {
        DbUtil.initialize();
        String q = "REPLACE INTO hotspot "
                 + "(id, district, address, detail, indoor, lat, lng) VALUES "
                 + "(?, ?, ?, ?, ?, ?, ?);";
        int updated = 0;
        Connection c = DbUtil.connect();
        c.setAutoCommit(false);
        PreparedStatement s = c.prepareStatement(q);
        for (Hotspot hotspot : JsonUtil.getEveryHotspot()) {
            s.setInt(1, hotspot.getId());
            s.setString(2, hotspot.getDistrict());
            s.setString(3, hotspot.getAddress());
            s.setString(4, hotspot.getDetail());
            s.setString(5, hotspot.getIndoor());
            s.setDouble(6, hotspot.getLat());
            s.setDouble(7, hotspot.getLng());
            s.addBatch();
            updated++;
        }
        s.executeBatch();
        c.commit();
        c.setAutoCommit(true);
        s.close();
        c.close();
        return updated;
    }

    protected Hotspot[] find(double latitude, double longitude, int limit) {
        String q = "SELECT lat, lng\n"
                 + "  FROM hotspot\n"
                 + " WHERE lat between ? AND ?\n"
                 + "   AND lng between ? AND ?;";
        String r = "SELECT id, district, address, detail, indoor, lat, lng\n"
                 + "  FROM hotspot\n"
                 + " WHERE lat between ? AND ?\n"
                 + "   AND lng between ? AND ?;";
        String h = "INSERT INTO history (lat, lng, requested_at) VALUES (?, ?, datetime('now', 'localtime'));";
        Hotspot[] hotspots = new Hotspot[limit];
        try (Connection c = DbUtil.connect()) {
            PreparedStatement s = c.prepareStatement(q);
            double range = findRange(s, latitude, longitude, limit);
            s = c.prepareStatement(r);
            s.setDouble(1, latitude - range);
            s.setDouble(2, latitude + range);
            s.setDouble(3, longitude - range);
            s.setDouble(4, longitude + range);
            ResultSet rs = s.executeQuery();
            ArrayList<Hotspot> candidates = new ArrayList<>();
            while (rs.next()) {
                Hotspot cand = new Hotspot(rs.getInt("id"),
                        rs.getString("district"), rs.getString("address"),
                        rs.getString("detail"), rs.getString("indoor"),
                        rs.getDouble("lat"), rs.getDouble("lng"));
                cand.setDist(latitude, longitude);
                candidates.add(cand);
            }
            candidates.sort((x, y) -> (int) (x.getDist() * 10000 - y.getDist() * 10000));
            for (int i = 0; i < limit; i++) hotspots[i] = candidates.get(i);
            s = c.prepareStatement(h);
            s.setDouble(1, latitude);
            s.setDouble(2, longitude);
            s.executeUpdate();
            s.close();
            c.close();
        } catch (NamingException | SQLException e) {
            e.printStackTrace();
        }
        return hotspots;
    }

    protected Hotspot[] history() throws SQLException, NamingException {
        String q = "SELECT * FROM history\n"
                 + " ORDER BY id DESC\n"
                 + "LIMIT 20;";
        Connection c = DbUtil.connect();
        Statement s = c.createStatement();
        ResultSet rs = s.executeQuery(q);
        ArrayList<Hotspot> tmp = new ArrayList<>();
        while (rs.next())
            tmp.add(new Hotspot(rs.getInt("id"), rs.getDouble("lat"),
                    rs.getDouble("lng"), rs.getString("requested_at")));
        Hotspot[] histories = new Hotspot[tmp.size()];
        for (int i = 0; i < histories.length; i++) histories[i] = tmp.get(i);
        s.close();
        c.close();
        return histories;
    }

    protected void delete(int id) throws SQLException, NamingException {
        String q = "DELETE FROM history WHERE id = ?;";
        Connection c = DbUtil.connect();
        PreparedStatement s = c.prepareStatement(q);
        s.setInt(1, id);
        s.executeUpdate();
        s.close();
        c.close();
    }

    private static double findRange(PreparedStatement s, double lat, double lng, int limit) throws SQLException {
        double low = 0, high = Math.max(Math.abs(37.566 - lat), Math.abs(126.9784 - lng)) + 2;
        int limiter = 0;
        while (low < high && limiter++ < 100) {
            double range = (high - low) / 2 + low;
            s.setDouble(1, lat - range);
            s.setDouble(2, lat + range);
            s.setDouble(3, lng - range);
            s.setDouble(4, lng + range);
            ResultSet rs = s.executeQuery();
            int count = 0;
            while (rs.next()) if (++count == limit * 5) break;
            if (count >= limit && count < limit * 5) return range;
            else if (count < 20) low = range + 0.0000000001;
            else high = range - 0.0000000001;
        }
        return high;
    }
}
