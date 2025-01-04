package io.github.emcw.utils.http.oapi.v3;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.emcw.utils.GsonUtil;

public class RequestBodyV3 {
    JsonObject body = new JsonObject();

    public RequestBodyV3(JsonElement customQuery) {
        body.add("query", customQuery);
    }

    public RequestBodyV3(String[] ids) {
        body.add("query", GsonUtil.arrFromStrArr(ids));
    }

//    public RequestBodyV3(Entity[] entities) {
//        body.add("query", );
//    }
//
//    public RequestBodyV3(DiscordReqObj[] discordReqObjs) {
//        body.add("query", );
//    }

    public String asString() {
        return GsonUtil.serialize(body);
    }
}