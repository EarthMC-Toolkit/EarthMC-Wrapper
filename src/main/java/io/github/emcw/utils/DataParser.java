package io.github.emcw.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
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
    static ConcurrentHashMap<String, JsonObject> towns = new ConcurrentHashMap<>();
    static ConcurrentHashMap<String, JsonObject> nations = new ConcurrentHashMap<>();

    static Safelist whitelist = new Safelist().addAttributes("a", "href");

    static List<String> processFlags(String str) {
        return Stream.of(str.split("<br />"))
                .parallel().map(e -> Jsoup.clean(e, whitelist))
                .collect(Collectors.toList());
    }

    public static JsonObject get() {
        JsonObject result = new JsonObject();
        result.add("towns", toObj(towns));
        result.add("nations", toObj(nations));

        return result;
    }

    public static void parse(String map) {
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

            if (nation != null) {
                nations.computeIfAbsent(nation.getAsString(), k -> {
                    JsonObject obj = new JsonObject();
                    obj.addProperty("name", nation.getAsString());

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

    public static Map<String, Town> toMap(JsonObject towns) {
        var itr = towns.entrySet().iterator();
        Map<String, Town> map = new HashMap<>();

        while (itr.hasNext()) {
            Map.Entry<String, JsonElement> next;
            try { next = itr.next(); }
            catch (NoSuchElementException e) {
                continue;
            }

            JsonObject info = next.getValue().getAsJsonObject();
            map.put(next.getKey(), new Town(info));
        }

        return map;
    }

    public static Map<String, Town> toMapParallel(JsonObject towns) {
        List<Map.Entry<String, JsonElement>> entries = new ArrayList<>(towns.entrySet());
        return entries.parallelStream().map(entry -> {
            try {
                JsonObject info = entry.getValue().getAsJsonObject();
                return Map.entry(entry.getKey(), new Town(info));
            } catch (Exception e) {
                System.out.print(e);
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
