package io.github.emcw.classes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.emcw.objects.Town;
import io.github.emcw.utils.API;
import io.github.emcw.utils.GsonUtil;
import io.github.emcw.interfaces.Collective;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Safelist;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Towns implements Collective<Town> {
    public Map<String, Town> cache = null;
    String map;

    public Towns(String mapName) {
        this.map = mapName;
        updateCache();
    }

    public Town single(String key) throws NullPointerException {
        return Collective.super.single(key, this.cache);
    }

    public List<Town> all() {
        return Collective.super.all(this.cache);
    }

    public void updateCache() {
        updateCache(false);
    }

    public void updateCache(Boolean force) {
        if (this.cache != null && !force) return;

        // Convert to Town objects and use as cache.
        JsonObject towns = getParsed().getAsJsonObject("towns");
        this.cache = toMapParallel(towns);
    }

    Safelist whitelist = new Safelist().addAttributes("a", "href");
    List<String> processFlags(String str) {
        return Arrays.stream(str.split("<br />"))
                .parallel().map(e -> Jsoup.clean(e, whitelist))
                .collect(Collectors.toList());
    }

    public JsonObject getParsed() {
        Map<String, JsonElement> mapData = API.mapData(this.map);
        Collection<JsonElement> areas = mapData.values();
        if (areas.size() < 1) return null;

        ConcurrentHashMap<String, JsonObject>
                towns = new ConcurrentHashMap<>(),
                nations = new ConcurrentHashMap<>();

        areas.parallelStream().forEach(town -> {
            JsonObject cur = town.getAsJsonObject();

            String name = GsonUtil.keyAsStr(cur, "label");
            if (name == null) return;

            String desc = GsonUtil.keyAsStr(cur, "desc");
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

//            nations.computeIfAbsent(nation.getAsString(), k -> {
//               JsonObject obj = new JsonObject();
//
//
//                return obj;
//            });
        });

        JsonObject result = new JsonObject();
        result.add("towns", toObj(towns));
        result.add("nations", toObj(nations));

        return result;
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