package io.github.emcw.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.emcw.objects.Nation;
import io.github.emcw.objects.Player;
import io.github.emcw.objects.Town;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Safelist;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.github.emcw.utils.GsonUtil.keyAsStr;

public class DataParser {
    static ConcurrentHashMap<String, JsonObject>
            towns   = new ConcurrentHashMap<>(),
            nations = new ConcurrentHashMap<>(),
            players = new ConcurrentHashMap<>();

    static Safelist whitelist = new Safelist().addAttributes("a", "href");

    static List<String> processFlags(String str) {
        return Stream.of(str.split("<br />")).parallel()
                .map(e -> Jsoup.clean(e, whitelist))
                .collect(Collectors.toList());
    }

    public static JsonObject get() {
        JsonObject result = new JsonObject();
        result.add("towns", toObj(towns));
        result.add("nations", toObj(nations));
        result.add("players", toObj(players));

        return result;
    }

    public static void parsePlayerData(String map) {
    
    }

    public static void parseMapData(String map, Boolean parseNations) {
        Map<String, JsonElement> mapData = API.mapData(map);
        Collection<JsonElement> areas = mapData.values();
        if (areas.size() < 1) return;

        areas.parallelStream().forEach(town -> {
            JsonObject cur = town.getAsJsonObject();

            String name = keyAsStr(cur, "label");
            if (name == null) return;

            String desc = keyAsStr(cur, "desc");
            if (desc == null) return;

            List<String> info = processFlags(desc);
            String title = info.get(0);

            if (title.contains("(Shop)")) return;
            info.remove("Flags");

            //System.out.println(info);

            Element link = Jsoup.parse(title).select("a").first();

            String nationStr = link != null ? link.text() : StringUtils.substringBetween(title, "(", ")");
            JsonElement nation = Objects.equals(nationStr, "")
                    ? null : GsonUtil.deserialize(nationStr, JsonElement.class);

            String wiki = link != null ? link.attr("href") : null;
            String mayor = info.get(1).replace("Mayor ", "");

            String names = StringUtils.substringBetween(String.join(", ", info), "Members ", ", pvp");
            String[] members = names.split(", ");

            towns.computeIfAbsent(name, k -> {
                JsonObject obj = new JsonObject();

                obj.addProperty("name", name);
                obj.addProperty("mayor", mayor);
                obj.addProperty("wiki", wiki);
                obj.add("nation", nation);
                obj.add("residents", GsonUtil.arrFromStrArr(members));

                return obj;
            });

            if (!parseNations) return;
            if (nation != null) {
                nations.computeIfPresent(nation.getAsString(), (k, v) -> {
                    v.getAsJsonArray("towns").add(towns.get(name));
                    return v;
                });

                nations.computeIfAbsent(nation.getAsString(), k -> {
                    JsonObject obj = new JsonObject();
                    obj.addProperty("name", nation.getAsString());

                    JsonArray townArr = new JsonArray();
                    townArr.add(towns.get(name));
                    obj.add("towns", townArr);

                    return obj;
                });
            }
        });
    }

    public static JsonObject toObj(Map<String, JsonObject> map) {
        JsonObject obj = new JsonObject();
        map.forEach(obj::add);
        return obj;
    }

    static Stream<Map.Entry<String, JsonElement>> streamEntries(JsonObject o) {
        return new ArrayList<>(o.entrySet()).parallelStream();
    }

    static <T> Map<String, T> collectAsMap(Stream<Map.Entry<String, T>> stream) {
        return stream.filter(Objects::nonNull).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public static Map<String, Player> playersAsMap(JsonObject players) {
        return collectAsMap(streamEntries(players).map(entry -> {
            try {
                JsonObject info = entry.getValue().getAsJsonObject();
                return Map.entry(entry.getKey(), new Player(info));
            } catch (Exception e) { return null; }
        }));
    }

    public static Map<String, Town> townsAsMap(JsonObject towns) {
        return collectAsMap(streamEntries(towns).map(entry -> {
            try {
                JsonObject info = entry.getValue().getAsJsonObject();
                return Map.entry(entry.getKey(), new Town(info));
            } catch (Exception e) { return null; }
        }));
    }

    public static Map<String, Nation> nationsAsMap(JsonObject nations) {
        return collectAsMap(streamEntries(nations).map(entry -> {
            try {
                JsonObject info = entry.getValue().getAsJsonObject();
                return Map.entry(entry.getKey(), new Nation(info));
            } catch (Exception e) { return null; }
        }));
    }
}
