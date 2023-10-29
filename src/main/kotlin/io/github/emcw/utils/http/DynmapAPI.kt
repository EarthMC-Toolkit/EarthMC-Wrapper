package io.github.emcw.utils.http

import com.google.gson.JsonObject
import org.jetbrains.annotations.Contract
import java.util.concurrent.*

/**
 * Utility class for interacting with the [EarthMC Dynmap](earthmc.net/map/aurora/) asynchronously.<br></br><br></br>
 * **Note:**
 * <br></br>This class is used internally to obtain fresh data, you should never need to use it directly.
 */
object DynmapAPI {
    @Contract("_, _ -> new")
    private operator fun get(map: String, key: String): CompletableFuture<JsonObject> {
        return CompletableFuture.supplyAsync {
            try {
                val endpoint = Request.getEndpoints().getIfPresent(key)
                if (endpoint != null) return@supplyAsync Request.send<JsonObject>(
                    endpoint[map].asString
                )
                throw NullPointerException("Error fetching $key! Received `null` as endpoint URL.")
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

    fun configData(mapName: String): JsonObject {
        return DynmapAPI[mapName, "config"].join()
    }

    fun playerData(mapName: String): JsonObject {
        return DynmapAPI[mapName, "players"].join()
    }

    fun mapData(mapName: String): JsonObject {
        val data = DynmapAPI[mapName, "map"]
        try {
            return data.join().getAsJsonObject("sets")
                .getAsJsonObject("townyPlugin.markerset")
                .getAsJsonObject("areas")
        } catch (e: Exception) {
            println(
                """Error fetching $mapName map data!
Received response may be incorrectly formatted."""
            )
        }
        return JsonObject()
    }
}