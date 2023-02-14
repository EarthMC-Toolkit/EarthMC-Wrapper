package net.emc.emcw.objects;

import com.google.gson.JsonObject;
import static net.emc.emcw.utils.GsonUtil.keyAsStr;

public class Resident extends Player {
    public final String town, nation, rank;

    Resident(JsonObject obj) {
        super(obj);

        this.town = keyAsStr(obj, "town");
        this.nation = keyAsStr(obj, "nation");
        this.rank = keyAsStr(obj, "rank");
    }
}