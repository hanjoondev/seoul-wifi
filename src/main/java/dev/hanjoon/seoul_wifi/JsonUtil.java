package dev.hanjoon.seoul_wifi;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class JsonUtil {
    private static final String baseUrl = "http://openapi.seoul.go.kr:8088/"
                                        + "774b49504d68616e39354b534a774b"
                                        + "/json/TbPublicWifiInfo/";
    private static Gson g = new Gson();

    protected static String dl(int s, int e) throws IOException {
        Request request = new Request.Builder().url(baseUrl + s + "/" + e).build();
        try (Response response = new OkHttpClient().newCall(request).execute()) {
            return response.body().string();
        }
    }

    protected static int getCount(String singleEntryResponse) {
        return g.fromJson(singleEntryResponse, JsonObject.class).getAsJsonObject()
                .get("TbPublicWifiInfo").getAsJsonObject().get("list_total_count").getAsInt();
    }

    protected static JsonArray getRows(String response) {
        return g.fromJson(response, JsonObject.class).getAsJsonObject()
                .get("TbPublicWifiInfo").getAsJsonObject().get("row").getAsJsonArray();
    }

    protected static Hotspot[] getEveryHotspot() throws IOException {
        int count = getCount(dl(1, 1));
        int q = count / 1000, r = count % 1000;
        Hotspot[] hotspots = new Hotspot[count];
        for (int i = 0; i <= q; i++) {
            int limit = i == q ? i * 1000 + r : (i + 1) * 1000, j = i * 1000;
            for (JsonElement row : getRows(dl(i * 1000 + 1, limit)))
                hotspots[j++] = getHotspot(j, row.getAsJsonObject());
        }
        return hotspots;
    }

    private static Hotspot getHotspot(int id, JsonObject o) {
        return new Hotspot(id,
                o.get("X_SWIFI_WRDOFC").getAsString(),
                o.get("X_SWIFI_ADRES1").getAsString(),
                o.get("X_SWIFI_ADRES2").getAsString(),
                o.get("X_SWIFI_INOUT_DOOR").getAsString(),
                o.get("LNT").getAsDouble(),
                o.get("LAT").getAsDouble());
    }
}
