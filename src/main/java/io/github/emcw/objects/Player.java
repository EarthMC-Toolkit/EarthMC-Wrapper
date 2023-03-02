package io.github.emcw.objects;

import com.google.gson.JsonObject;

import static io.github.emcw.utils.GsonUtil.*;

public class Player {
    public final String name, nickname, world;
    public final Location location;

    public Player(JsonObject obj, Boolean resident) {
        this.name = keyAsStr(obj, "name");
        this.nickname = keyAsStr(obj, "nickname");

        this.world = keyAsStr(obj, "world");
        this.location = null;
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
}