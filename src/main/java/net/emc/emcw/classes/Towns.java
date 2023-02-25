package net.emc.emcw.classes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.emc.emcw.interfaces.Collective;
import net.emc.emcw.objects.Town;
import net.emc.emcw.utils.API;
import net.emc.emcw.utils.GsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Safelist;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static net.emc.emcw.utils.GsonUtil.keyAsStr;

public class Towns implements Collective<Town> {
    public Map<String, Town> cache = null;
    String map;

    public Towns(String mapName) {
        this.map = mapName;
        tryUpdateCache();
    }

    public Town single(String key) throws NullPointerException {
        //tryUpdateCache();
        return Collective.super.single(key, this.cache);
    }

    public List<Town> all() {
        //tryUpdateCache();
        return Collective.super.all(this.cache);
    }

    public void tryUpdateCache() {
        if (this.cache != null) return;

        // Convert to Town objects and use as cache.
        JsonObject towns = getParsed().getAsJsonObject("towns");
        this.cache = toMap(towns);
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