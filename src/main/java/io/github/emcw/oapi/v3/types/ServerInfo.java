package io.github.emcw.oapi.v3.types;

import com.google.gson.JsonObject;
import io.github.emcw.interfaces.ISerializable;
import org.jetbrains.annotations.Nullable;

import static io.github.emcw.utils.GsonUtil.keyAsStr;

@SuppressWarnings("unused")
public class ServerInfo implements ISerializable {
    @Nullable public final String version;

    /**
     * The current phase of the moon.
     * @see <a href="https://jd.papermc.io/paper/1.21.4/io/papermc/paper/world/MoonPhase.html">/io/papermc/paper/world/MoonPhase</a>
     */
    @Nullable public final String moonPhase;

    public ServerInfo(JsonObject resp) {
        this.version = keyAsStr(resp, "version");
        this.moonPhase = keyAsStr(resp, "moonPhase");
    }
}