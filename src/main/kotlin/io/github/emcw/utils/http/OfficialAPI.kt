package io.github.emcw.utils.http

import com.google.gson.JsonObject
import org.jetbrains.annotations.Contract
import java.util.concurrent.*

class OfficialAPI {
    private fun townyData(endpoint: String): JsonObject? {
        val fullUrl = OAPI_DOMAIN + if (endpoint.startsWith("/")) endpoint.substring(1) else endpoint
        val data = Companion[fullUrl]
        try {
            return data.join().asJsonObject
        } catch (e: Exception) {
            println(
                """
                    Error fetching OAPI data from $fullUrl
                    Received response may be incorrectly formatted.
                    """.trimIndent()
            )
        }
        return null
    }

    fun serverInfo(): JsonObject? {
        // TODO: Proper deserialization of API response. (Using records?)
        return townyData("")

        //return null;
    }

    protected class Towns
    companion object {
        const val OAPI_DOMAIN = "https://api.earthmc.net/v2/aurora/"
        @Contract("_, -> new")
        private operator fun get(url: String): CompletableFuture<JsonObject> {
            return CompletableFuture.supplyAsync {
                try {
                    return@supplyAsync Request.send<JsonObject>(url)
                } catch (e: Exception) {
                    println(
                        """
    Exception occurred!
    ${e.message}
    """.trimIndent()
                    )
                    return@supplyAsync null
                }
            }
        }
    }
}