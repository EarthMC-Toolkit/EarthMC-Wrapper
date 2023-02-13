package net.emc.emcw.objects;

import com.google.gson.JsonObject;

public class Player {
    String name;

    public Player(JsonObject obj) {
        this.name = obj.get("name").getAsString();
    }

    public Player() {
    }
}
