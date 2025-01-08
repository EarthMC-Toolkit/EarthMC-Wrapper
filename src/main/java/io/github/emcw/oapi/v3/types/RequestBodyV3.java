package io.github.emcw.oapi.v3.types;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.emcw.utils.GsonUtil;
import org.jetbrains.annotations.NotNull;

public class RequestBodyV3 {
    JsonObject body = new JsonObject();

    public RequestBodyV3(JsonElement queryElement) {
        body.add("query", queryElement);
    }

    public RequestBodyV3(String[] ids) {
        this(GsonUtil.arrAsJsonArray(ids));
    }

    public RequestBodyV3(DiscordReqObj[] objs) {
        this(GsonUtil.arrAsJsonArray(objs));
    }

    public RequestBodyV3(Point2D[] points) {
        this(queryFromPoints(points));
    }

    private static @NotNull JsonArray queryFromPoints(Point2D[] points) {
        JsonArray query = new JsonArray();
        for (Point2D point : points) {
            JsonArray ints = new JsonArray();
            ints.add(point.X);
            ints.add(point.Z);
            query.add(ints);
        }

        return query;
    }

    public String asString() {
        return GsonUtil.serialize(body);
    }
}