package dev.hanjoon.seoulwifi;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DbUtil {
    protected static Connection connect() throws NamingException {
        Connection c = null;
        try {
            DataSource ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/seoul-wifi-sqlite");
            c = ds.getConnection();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return c;
    }

    protected static void initialize() throws NamingException, SQLException {
        Connection c = connect();
        c.setAutoCommit(false);
        Statement s = c.createStatement();
        s.executeUpdate(getCreateTableHotspotQuery());
        s.executeUpdate(getCreateTableHistoryQuery());
        c.commit();
        c.setAutoCommit(true);
        s.close();
        c.close();
    }

    private static String getCreateTableHotspotQuery() {
        StringBuilder q = new StringBuilder();
        q.append("CREATE TABLE IF NOT EXISTS hotspot (\n");
        q.append("             id INTEGER PRIMARY KEY\n");
        q.append("           , district TEXT\n");
        q.append("           , address TEXT\n");
        q.append("           , detail TEXT\n");
        q.append("           , indoor TEXT\n");
        q.append("           , lat REAL\n");
        q.append("           , lng REAL);");
        return q.toString();
    }

    private static String getCreateTableHistoryQuery() {
        StringBuilder q = new StringBuilder();
        q.append("CREATE TABLE IF NOT EXISTS history (\n");
        q.append("             id INTEGER PRIMARY KEY AUTOINCREMENT\n");
        q.append("           , lat REAL NOT NULL\n");
        q.append("           , lng REAL NOT NULL\n");
        q.append("           , requested_at TEXT NOT NULL);");
        return q.toString();
    }
}
