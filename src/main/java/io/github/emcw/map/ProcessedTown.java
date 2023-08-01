package io.github.emcw.map;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

import java.util.List;
import java.util.stream.Collectors;

import static io.github.emcw.utils.Funcs.calcArea;
import static io.github.emcw.utils.GsonUtil.*;

@SuppressWarnings("unused")
public class ProcessedTown {
    public final String name, desc;
    public final String fill, outline;

    public final int[] x, z;
    public final int area;

    public final List<String> info;

    static final Safelist whitelist = new Safelist().addAttributes("a", "href");

    public ProcessedTown(JsonObject obj) {
        this.name = keyAsStr(obj, "label");
        this.desc = keyAsStr(obj, "desc");

        this.info = this.desc != null ? processFlags(this.desc) : null;

        this.fill = keyAsStr(obj, "fillcolor");
        this.outline = keyAsStr(obj, "color");

        this.x = arrToIntArr(keyAsArr(obj, "x"));
        this.z = arrToIntArr(keyAsArr(obj, "z"));
        this.area = calcArea(this.x, this.z);
    }

    public static List<String> processFlags(@NotNull String str) {
        return strArrAsStream(str.split("<br />"))
                .map(e -> Jsoup.clean(e, whitelist))
                .collect(Collectors.toList());
    }
}