package net.emc.emcw.classes;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.emc.emcw.interfaces.Collective;
import net.emc.emcw.objects.parsed.Town;
import net.emc.emcw.utils.API;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static net.emc.emcw.utils.GsonUtil.keyAsStr;

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
        Map<String, JsonElement> mapData = API.mapData(this.map);
        Map<String, List<String>> obj = new HashMap<>();

        for (JsonElement town : mapData.values()) {
            JsonObject cur = town.getAsJsonObject();
            String desc = keyAsStr(cur, "desc");

            Safelist whitelist = new Safelist().addAttributes("a", "href");
            List<String> raw = Arrays.stream(desc.split("<br />"))
                    .map(e -> Jsoup.clean(e, whitelist)).collect(Collectors.toList());

            if (raw.get(0).contains("(Shop)")) continue;

            raw.remove("Flags");
            obj.put(keyAsStr(cur, "label"), raw);
        }

        return obj;
    }

    public void updateCache(JsonObject data) {
        Map<String, List<String>> parsed = getParsed();

        // Push town objects to cache
        // cache = fromArray();
    }
}