package net.emc.emcw.classes;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.emc.emcw.interfaces.Collective;
import net.emc.emcw.objects.Town;
import net.emc.emcw.utils.API;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Towns implements Collective<Town> {
    public Map<String, Town> cache = null;
    String map;

    public Towns(String mapName) {
        this.map = mapName;
    }

    public static List<Town> fromArray(JsonArray arr) {
        return arr.asList().stream()
                .map(p -> new Town(p.getAsJsonObject()))
                .collect(Collectors.toList());
    }

    public Map<String, List<String>> getParsed() {
        //List<Town> towns = new ArrayList<>();
        Map<String, JsonElement> mapData = API.mapData(this.map);
        Map<String, List<String>> obj = new HashMap<>();

        for (Map.Entry<String, JsonElement> town : mapData.entrySet()) {
            JsonObject cur = town.getValue().getAsJsonObject();

            String desc = cur.get("desc").getAsString();

            Safelist whitelist = new Safelist().addAttributes("a", "href");
            List<String> raw = Arrays.stream(desc.split("<br />"))
                    .map(e -> Jsoup.clean(e, whitelist)).collect(Collectors.toList());

            if (raw.get(0).contains("(Shop)")) continue;

            raw.remove("Flags");
            obj.put(town.getKey(), raw);
        }

        return obj;
    }

    public void updateCache(JsonObject data) {
        Map<String, List<String>> parsed = getParsed();

        // Push town objects to cache
        // cache = fromArray();
    }
}