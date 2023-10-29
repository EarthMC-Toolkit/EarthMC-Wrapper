package io.github.emcw.utils.http

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import io.github.emcw.exceptions.APIException
import lombok.SneakyThrows
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import okhttp3.OkHttpClient.Builder.addInterceptor
import okhttp3.OkHttpClient.Builder.build
import okhttp3.OkHttpClient.Builder.callTimeout
import okhttp3.OkHttpClient.Builder.connectionPool
import okhttp3.OkHttpClient.Builder.protocols
import okhttp3.Protocol
import okhttp3.Request.Builder.build
import okhttp3.Request.Builder.url
import okhttp3.Response
import okhttp3.brotli.BrotliInterceptor
import java.util.List
import java.util.concurrent.TimeUnit

object Request {
    val builder: Builder = Builder()
    private val client: OkHttpClient = Builder()
        .callTimeout(10, TimeUnit.SECONDS)
        .connectionPool(ConnectionPool(16, 3, TimeUnit.MINUTES))
        .addInterceptor(BrotliInterceptor)
        .protocols(List.of<Protocol>(Protocol.HTTP_2, Protocol.HTTP_1_1))
        .build()
    val codes = List.of(*arrayOf(200, 203, 304))
    const val epUrl = "https://raw.githubusercontent.com/EarthMC-Toolkit/EarthMC-NPM/main/src/endpoints.json"
    val endpoints = Caffeine.newBuilder().build<String?, JsonObject?>()
    fun getEndpoints(): Cache<String?, JsonObject?> {
        if (endpoints.asMap().isEmpty()) {
            val eps = updateEndpoints()
            eps?.asMap()?.forEach { (k: String?, v: JsonElement) -> endpoints.put(k, v.asJsonObject) }
        }
        return endpoints
    }

    fun updateEndpoints(): JsonObject? {
        return send(epUrl)
    }

    fun <T> send(url: String): T {
        return JsonParser.parseString(fetch(url)) as T
    }

    @SneakyThrows
    fun fetch(urlString: String): String {
        builder.url(urlString)
        val response: Response
        val endpointStr = "\nEndpoint: $urlString"
        try {
            response = client.newCall(builder.build()).execute()
            val statusCode = response.code
            if (!codes.contains(statusCode)) throw APIException(
                "API Error! Response code: $statusCode$endpointStr"
            )
            response.body.use { body ->
                if (body == null) throw APIException("Fetch Error: Response body is null")
                return body.string()
            }
        } catch (e: Exception) {
            throw APIException("Request failed! " + endpointStr + e.message)
        }
    }
}