package dev.hanjoon.seoul_wifi;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Base64;
import java.util.HashMap;

public class SqliteHelper {
    protected static Connection connect() throws NamingException {
        Connection c = null;
        try {
            Context ctx = new InitialContext();
            DataSource ds = (DataSource) ctx.lookup("java:comp/env/jdbc/seoulwifisqlite");
            c = ds.getConnection();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return c;
    }

    protected static int insert(String[] keys, int numEntries)
                    throws IOException, NoSuchAlgorithmException, SQLException, NamingException {
        JsonArray[] arr = JsonHandler.getAllJsons(numEntries);
        HashMap<Integer, String> hashesFromDb = getExistingHashes();
        String joinedKeys = String.join(", ", keys);
        int updated = 0, wifiId = 1, hashId = 0;
        Connection c = connect();
        c.setAutoCommit(false);
        Statement s = c.createStatement();
        for (JsonArray a : arr) {
            String hash = sha256(a.toString());
            if (hash.equals(hashesFromDb.get(++hashId))) {
                wifiId += 1000;
                continue;
            }
            int tmp = wifiId;
            s.executeUpdate(getHashInsertQuery(hashId, hash));
            for (JsonElement elem : a)
                s.executeUpdate(getWifiInsertQuery(wifiId++, elem.getAsJsonObject(), joinedKeys, keys));
            updated += wifiId - tmp;
        }
        s.close();
        c.commit();
        c.close();
        return updated;
    }

    private static HashMap<Integer, String> getExistingHashes() throws SQLException, NamingException {
        HashMap<Integer, String> h = new HashMap<>();
        Connection c = connect();
        Statement s = c.createStatement();
        ResultSet rs = s.executeQuery("SELECT id, hash FROM hashes;");
        while(rs.next())
            h.put(rs.getInt("id"), rs.getString("hash"));
        s.close();
        c.close();
        return h;
    }

    private static String getHashInsertQuery(int id, String hash) {
        return "REPLACE INTO hashes (id,hash) VALUES ('" + id + "', '" + hash + "');";
    }

    private static String getWifiInsertQuery(int id, JsonObject o, String joinedKeys, String[] keys) {
        StringBuilder sb = new StringBuilder("REPLACE INTO wifi (id,");
        sb.append(joinedKeys).append(") VALUES ('").append(id);
        for (int i = 0; i < 4; i++) sb.append("','").append(o.get(keys[i]).getAsString().replace("'", ""));
        for (int i = 4; i < 6; i++) sb.append("','").append(o.get(keys[i]).getAsDouble());
        return sb.append("');").toString();
    }

    private static String sha256(String s) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(s.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hash);
    }
}
