package net.emc.emcw.objects;

import com.google.gson.JsonObject;

import java.awt.*;
import java.util.List;
import java.util.Objects;

import static net.emc.emcw.utils.GsonUtil.keyAsInt;
import static net.emc.emcw.utils.GsonUtil.keyAsStr;
import static net.emc.emcw.utils.GsonUtil.keyAsBool;

public class Town {
    String name, mayor, nation;
    Integer area;

    Location location;
    List<Resident> residents;

    Color fill, outline;
    Flags flags;

    public Town(JsonObject obj) {
        this.name = keyAsStr(obj, "name");
        this.mayor = keyAsStr(obj, "mayor");
        this.area = keyAsInt(obj, "area");

        this.location = Location.fromObj(obj);
        this.residents = Resident.fromArray(obj.getAsJsonArray("residents"));

        String fillHex = keyAsStr(obj, "fillcolor");
        String outlineHex = keyAsStr(obj, "color");

        this.fill = getColour(fillHex);
        this.outline = getColour(outlineHex);

        this.flags = new Flags(obj);
    }

    static class Flags {
        Boolean PVP, EXPLOSIONS, FIRE, CAPITAL, MOBS, PUBLIC;

        Flags(JsonObject obj) {
            this.PVP = keyAsBool(obj, "pvp");
            this.EXPLOSIONS = keyAsBool(obj, "explosion");
            this.FIRE = keyAsBool(obj, "fire");
            this.CAPITAL = keyAsBool(obj, "capital");
            this.MOBS = keyAsBool(obj, "mobs");
            this.PUBLIC = keyAsBool(obj, "public");
        }
    }

    public Color getColour(String hex) {
        return Color.decode(hex == null ? defaultColour() : hex);
    }

    private String defaultColour() {
        return Objects.equals(this.nation, "No Nation") ? "#89C500" : "3FB4FF";
    }
}