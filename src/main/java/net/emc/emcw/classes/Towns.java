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
        tryUpdateCache();
        return Collective.super.single(key, this.cache);
    }

    public List<Town> all() {
        tryUpdateCache();
        return Collective.super.all(this.cache);
    }

    public void tryUpdateCache() {
        if (this.cache != null) return;

        // Convert to Town objects and use as cache.
        JsonObject towns = parsedTowns();
        this.cache = toMapParallel(towns);
    }

    Safelist whitelist = new Safelist().addAttributes("a", "href");
    List<String> processFlags(String str) {
        return Arrays.stream(str.split("<br />")).parallel()
                .map(e -> Jsoup.clean(e, whitelist))
                .collect(Collectors.toList());
    }

    public JsonObject parsedTowns() {
        Map<String, JsonElement> mapData = API.mapData(this.map);

        Collection<JsonElement> areas = mapData.values();
        if (areas.size() < 1) return null;

        JsonObject all = new JsonObject();
        ConcurrentHashMap<String, JsonObject> towns = new ConcurrentHashMap<>();

        areas.parallelStream().forEach(town -> {
            JsonObject cur = town.getAsJsonObject();

            String name = keyAsStr(cur, "label");
            String desc = keyAsStr(cur, "desc");

            // There is no label or desc, town object is likely broken.
            if (name == null || desc == null) return;

            List<String> raw = processFlags(desc);

            String title = raw.get(0);
            if (raw.get(0).contains("(Shop)")) return;
            raw.remove("Flags");

            Element link = Jsoup.parse(title).select("a").first();

            JsonElement nation = null;
            String nationStr = link != null ? link.text() : StringUtils.substringBetween(title, "(", ")");
            if (!Objects.equals(nationStr, ""))
                nation = GsonUtil.deserialize(nationStr, JsonElement.class);

            String wiki = link != null ? link.attr("href") : null;
            String mayor = raw.get(1).replace("Mayor ", "");

            List<String> members = Arrays.stream(raw.get(2).replace("Members ", "").split(", ")).parallel().toList();

            JsonObject obj = new JsonObject();
            obj.addProperty("name", name);

            obj.add("nation", nation);
            obj.addProperty("wiki", wiki);
            obj.addProperty("mayor", mayor);
            obj.add("residents", GsonUtil.listToArr(members));

            towns.computeIfAbsent(name, k -> obj);
        });

        towns.forEach(all::add);
        return all;
    }

    public static Map<String, Town> toMap(JsonObject towns) {
        var itr = towns.entrySet().iterator();
        Map<String, Town> map = new HashMap<>();

        while (itr.hasNext()) {
            Map.Entry<String, JsonElement> next = null;
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
            } catch (Exception e) { return null; }
        }).filter(Objects::nonNull).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}