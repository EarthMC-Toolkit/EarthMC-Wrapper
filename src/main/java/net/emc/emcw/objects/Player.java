package net.emc.emcw.objects;

import com.google.gson.JsonObject;

import static net.emc.emcw.utils.GsonUtil.keyAsStr;

public class Player {
    public final String name, nickname;
    public final Location location;

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
            "\nLocation: " + location.x + ", " + location.y + ", " + location.z;
    }
}