package io.github.emcw.utils.http;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

class OkHttpCallback extends CompletableFuture<Response> implements Callback {
    @Override
    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
        super.complete(response);
    }

    @Override
    public void onFailure(@NotNull Call call, @NotNull IOException e) {
        super.completeExceptionally(e);
    }
}