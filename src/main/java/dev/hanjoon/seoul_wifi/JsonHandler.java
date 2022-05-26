package dev.hanjoon.seoul_wifi;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

public class JsonHandler {
    private static final String urlBase = "http://openapi.seoul.go.kr:8088/"
                                        + "774b49504d68616e39354b534a774b"
                                        + "/json/TbPublicWifiInfo/";
    private static Gson g = new Gson();

    protected static String dl(int s, int e) throws IOException {
        Request request = new Request.Builder()
                                     .url(urlBase + s + "/" + e)
                                     .addHeader("Content-type", "application/json; charset=utf-8")
                                     .build();
        try (Response response = new OkHttpClient().newCall(request).execute()) {
            return response.body().string();
        }
    }

    protected static int getCount(String single) {
        int i = single.indexOf("\"list_total_count\":");
        if ((i += 19) == 18) return -1;
        return Integer.parseInt(single.substring(i, i + single.substring(i).indexOf(",")));
    }

    protected static String[] getKeySet(String single) {
        List<String> keys = new ArrayList<>();
        JsonArray a = g.fromJson(single, JsonObject.class).getAsJsonObject()
                       .get("TbPublicWifiInfo").getAsJsonObject()
                       .get("row").getAsJsonArray();
        for (JsonElement elem : a)
            for (Entry<String, JsonElement> entry : elem.getAsJsonObject().entrySet())
                keys.add(entry.getKey());
        String[] res = new String[keys.size()];
        for (int i = 0; i < keys.size(); i++)
            res[i] = keys.get(i);
        return res;
    }

    protected static JsonArray[] getAllJsons(int numEntries) throws IOException {
        int q = numEntries / 1000, r = numEntries % 1000;
        JsonArray[] arr = new JsonArray[q + (r == 0 ? 0 : 1)];
        for (int i = 0; i <= q; i++) {
            String raw = dl(i * 1000 + 1, i == q ? i * 1000 + r : (i + 1) * 1000);
            JsonElement elem = g.fromJson(raw, JsonObject.class).getAsJsonObject()
                                .get("TbPublicWifiInfo").getAsJsonObject()
                                .get("row");
            arr[i] = elem.getAsJsonArray();
        }
        return arr;
    }
}
