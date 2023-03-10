package io.github.emcw.objects;

import com.google.gson.JsonObject;
import lombok.Getter;

import java.awt.*;
import java.util.List;
import java.util.Objects;

import static io.github.emcw.utils.GsonUtil.*;

public class Town extends Base<Town> {
    @Getter String mayor, nation;
    @Getter Integer area;
    @Getter Location location;
    @Getter List<Resident> residents;
    @Getter Flags flags;

    //public Color fill, outline;

    @Override
    public String toString() {
        return serialize(this);
    }

    public Town(JsonObject obj) {
        super();
        setInfo(this, keyAsStr(obj, "name"));

        this.nation = keyAsStr(obj, "nation");
        this.mayor = keyAsStr(obj, "mayor");
        this.residents = Resident.fromArr(obj.getAsJsonArray("residents"));

        this.location = Location.of(obj);
        this.area = keyAsInt(obj, "area");
        this.flags = new Flags(obj);

//        String fillHex = keyAsStr(obj, "fillcolor");
//        String outlineHex = keyAsStr(obj, "color");
//
//        this.fill = getColour(fillHex);
//        this.outline = getColour(outlineHex);
    }

    static class Flags {
        Boolean PVP, EXPLOSIONS, FIRE, CAPITAL, MOBS, PUBLIC;

        Flags(JsonObject obj) {
            this.PVP = keyAsBool(obj, "pvp");
            this.EXPLOSIONS = keyAsBool(obj, "explosions");
            this.FIRE = keyAsBool(obj, "fire");
            this.CAPITAL = keyAsBool(obj, "capital");
            this.MOBS = keyAsBool(obj, "mobs");
            this.PUBLIC = keyAsBool(obj, "public");
        }
    }

    public static List<Resident> onlineResidents(String mapName, Town town) {
        return town.getResidents().parallelStream().filter(p -> p.online(mapName)).toList();
    }

    public boolean hasNation() {
        return this.nation != null;
    }

    Color getColour(String hex) {
        return Color.decode(hex == null ? defaultColour() : hex);
    }

    String defaultColour() {
        return Objects.equals(this.nation, "No Nation") ? "#89C500" : "3FB4FF";
    }
}