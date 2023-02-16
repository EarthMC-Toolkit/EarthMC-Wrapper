package net.emc.emcw.objects;

import com.google.gson.JsonObject;
public class Player {
    public final String name, nickname;
    public final Location location;

    public Player(JsonObject obj) {
        this.name = obj.get("account").getAsString();
        this.nickname = obj.get("name").getAsString();

        Location loc = Location.fromObj(obj);
        this.location = loc;
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