package io.github.emcw.objects;

import com.google.gson.JsonObject;

import static io.github.emcw.utils.GsonUtil.*;

public class Player {
    public final String name, nickname;
    public final Location location;

    public Player(JsonObject obj, Boolean resident) {
        this.name = this.nickname = keyAsStr(obj, "name");
        this.location = null;
    }

    public Player(JsonObject obj) {
        this.name = keyAsStr(obj, "account");
        this.nickname = keyAsStr(obj, "name");
        this.location = Location.fromObj(obj);
    }

    boolean hidden() {
        Location loc = this.location;
        return loc.y == 64 && loc.x == 0 && loc.z == 0;
    }

    @Override
    public String toString() {
        return "Name: " + name +
            "\nNickname: " + nickname +
            "\nLocation: " + serialize(location);
    }
}