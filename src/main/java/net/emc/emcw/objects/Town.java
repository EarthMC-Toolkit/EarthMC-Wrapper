package net.emc.emcw.objects;

import com.google.gson.JsonObject;
import net.emc.emcw.classes.Players;

import java.awt.*;
import java.util.List;
import java.util.Objects;

import static net.emc.emcw.utils.GsonUtil.keyAsInt;
import static net.emc.emcw.utils.GsonUtil.keyAsStr;

public class Town {
    String name, mayor, nation;
    Integer area;

    Location location;
    List<Player> residents;

    Color fill, outline;
    JsonObject flags;

    public Town(JsonObject obj) {
        this.name = keyAsStr(obj, "name");
        this.mayor = keyAsStr(obj, "mayor");
        this.area = keyAsInt(obj, "area");

        this.location = Location.fromObj(obj);
        this.residents = Players.fromArray(obj.getAsJsonArray("residents"));

        String fillHex = keyAsStr(obj, "fillcolor");
        String outlineHex = keyAsStr(obj, "color");

        this.fill = getColour(fillHex);
        this.outline = getColour(outlineHex);
    }

    public Color getColour(String hex) {
        return Color.decode(hex == null ? defaultColour() : hex);
    }

    private String defaultColour() {
        return Objects.equals(this.nation, "No Nation") ? "#89C500" : "3FB4FF";
    }
}
