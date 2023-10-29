package io.github.emcw.caching

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import io.github.emcw.exceptions.MissingEntryException
import io.github.emcw.utils.GsonUtil
import lombok.AccessLevel
import lombok.Setter
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

@Suppress("unused")
open class BaseCache<V>
/**
 * Abstract class acting as a parent to other cache classes and holds a reference to a Caffeine cache.<br></br>
 * It provides the fundamental methods (get, single & all) that children automatically inherit.
 *
 * @param cacheOptions The options that this cache will be setup with.
 * @see io.github.emcw.caching.CacheOptions
 */(private val options: CacheOptions) {
    @Setter(AccessLevel.PROTECTED) protected var cache: Cache<String?, V?>? = null

    val builder = Caffeine.newBuilder()
    val CONCURRENCY = Runtime.getRuntime().availableProcessors()
    var service: ScheduledExecutorService? = null

    @Setter
    protected var updater: Runnable? = null
    protected fun build() {
        if (options.strategy != CacheStrategy.LAZY) {
            initRefreshScheduler()
            setCache(builder.build<String, V>())
        } else {
            setCache(builder.expireAfterWrite(options.expiry, options.unit).build<String, V>())
        }
    }

    private fun updateIf(strategy: CacheStrategy) {
        if (options.strategy == strategy) {
            updater!!.run() // Updates the cache before accessing it
        }
    }

    open operator fun get(vararg keys: String): Map<String?, V>? {
        val result: MutableMap<String?, V> = ConcurrentHashMap()
        GsonUtil.strArrAsStream(keys).forEach { k: String? ->
            val cur = _all()[k]
            if (cur != null) result[k] = cur
        }
        return result
    }

    @Throws(MissingEntryException::class)
    open fun single(key: String?): V {
        updateIf(CacheStrategy.HYBRID)
        var `val` = cache!!.getIfPresent(key)
        if (`val` == null) {
            // Expired and lazy, force update.
            updateIf(CacheStrategy.LAZY)
            `val` = cache!!.getIfPresent(key)
            if (`val` == null) throw MissingEntryException("Could not find entry by key '$key'")
        }
        return `val`
    }

    open fun all(): Map<String?, V?>? {
        return _all()
    }

    private fun _all(): Map<String?, V?> {
        val map: MutableMap<String?, V?> = TreeMap(java.lang.String.CASE_INSENSITIVE_ORDER)
        map.putAll(cache!!.asMap())
        return map
    }

    fun has(key: String?): Boolean {
        return if (cache!!.asMap().containsKey(key)) true else _all()[key] != null
    }

    private fun initRefreshScheduler() {
        service = Executors.newScheduledThreadPool(CONCURRENCY)
        service.scheduleAtFixedRate(Runnable {
            try {
                updater!!.run()
            } catch (e: Exception) {
                throw RuntimeException(e.message)
            }
        }, options.expiry, options.expiry, options.unit)
    }

    private fun stopRefreshScheduler() {
        service = null
    }

    fun clear() {
        cache!!.invalidateAll()
    }

    fun empty(): Boolean {
        return cache == null || cache!!.asMap().isEmpty()
    }

    fun put(key: String?, `val`: V) {
        cache!!.put(key, `val`)
    }

    fun putAll(map: Map<out String?, V?>?) {
        cache!!.putAll(map)
    }

    fun tryExpire() {
        if (options.strategy == CacheStrategy.LAZY || options.strategy == CacheStrategy.HYBRID) {
            clear()
        }
    }
}