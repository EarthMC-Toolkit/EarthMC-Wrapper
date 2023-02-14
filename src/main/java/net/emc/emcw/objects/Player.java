package net.emc.emcw.objects;

import com.google.gson.JsonObject;
public class Player {
    public final String name, nickname;

    public final Location location;
    public final Boolean hidden;

    public Player(JsonObject obj) {
        this.name = obj.get("account").getAsString();
        this.nickname = obj.get("name").getAsString();

        Location loc = Location.fromObj(obj);
        this.hidden = loc.x == 0 && loc.y == 64 && loc.z == 0;
        this.location = loc;
    }
}