package io.github.emcw.objects;

import com.google.gson.JsonObject;
import io.github.emcw.core.EMCMap;

import java.util.Objects;

import static io.github.emcw.core.EMCWrapper.instance;
import static io.github.emcw.utils.GsonUtil.*;

public class Player {
    public final String name, nickname;
    public Location location = null;

    String world = null;

    public Player(JsonObject obj, Boolean resident) {
        this.name = keyAsStr(obj, "name");
        this.nickname = keyAsStr(obj, "nickname");
    }

    public Player(JsonObject obj) {
        this.name = keyAsStr(obj, "name");
        this.nickname = keyAsStr(obj, "nickname");

        this.world = keyAsStr(obj, "world");
        this.location = Location.fromObj(obj);
    }

    boolean hidden() {
        Location loc = this.location;
        return loc.y == 64 && loc.x == 0 && loc.z == 0;
    }

    boolean underground() {
        return !Objects.equals(this.world, "earth");
    }

    public boolean online(String map) {
        return online(map, this.name);
    }

    public static boolean online(String mapName, String playerName) {
        EMCMap map = Objects.equals(mapName, "nova") ? instance().getNova() : instance().getAurora();
        return map.Players.getOnline(playerName) != null;
    }
}