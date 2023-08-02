package io.github.emcw.caching;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.emcw.exceptions.MissingEntryException;
import lombok.AccessLevel;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static io.github.emcw.utils.GsonUtil.strArrAsStream;

@SuppressWarnings("unused")
public class BaseCache<V> {
    @Setter(AccessLevel.PROTECTED) protected Cache<String, V> cache;
    protected final CacheOptions options;

    final Caffeine<Object, Object> builder = Caffeine.newBuilder();
    final Integer CONCURRENCY = Runtime.getRuntime().availableProcessors();

    ScheduledExecutorService service = null;
    @Setter protected Runnable updater = null;

    /**
    * Abstract class acting as a parent to other cache classes and holds a reference to a Caffeine cache.<br>
    * It provides the fundamental methods (get, single & all) that children automatically inherit.
    *
    * @param cacheOptions The options that this cache will be setup with.
    * @see  io.github.emcw.caching.CacheOptions
    */
    public BaseCache(CacheOptions cacheOptions) {
        options = cacheOptions;
    }

    protected void build() {
        if (options.strategy != CacheStrategy.LAZY) {
            initRefreshScheduler();
            setCache(builder.build());
        }
        else {
            setCache(builder.expireAfterWrite(options.expiry, options.unit).build());
        }
    }

    protected void updateIf(CacheStrategy strategy) {
        if (strategy == options.strategy) {
            updater.run(); // Updates the cache before accessing it
        }
    }

    public Map<String, V> get(String @NotNull ... keys) {
        Map<String, V> result = new ConcurrentHashMap<>();

        strArrAsStream(keys).forEach(k -> {
            V cur = all().get(k);
            if (cur != null)
                result.put(k, cur);
        });

        return result;
    }

    public V single(String key) throws MissingEntryException {
        updateIf(CacheStrategy.HYBRID);

        V val = cache.getIfPresent(key);
        if (val == null) {
            // Expired and lazy, force update.
            System.out.println("expired, force updating");
            updateIf(CacheStrategy.LAZY);

            val = cache.getIfPresent(key);
            if (val == null) throw new MissingEntryException("Could not find entry by key '" + key + "'");
        }

        return val;
    }

    public Map<String, V> all() {
        return _all();
    }

    private @NotNull Map<String, V> _all() {
        Map<String, V> map = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        map.putAll(cache.asMap());

        return map;
    }

    public boolean has(String key) {
        if (cache.asMap().containsKey(key)) return true;
        else return all().get(key) != null;
    }

    private void initRefreshScheduler() {
        service = Executors.newScheduledThreadPool(CONCURRENCY);
        service.scheduleAtFixedRate(() -> {
            try { updater.run(); }
            catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        }, options.expiry, options.expiry, options.unit);
    }

    private void stopRefreshScheduler() {
        service = null;
    }

    public void clear() {
        cache.invalidateAll();
    }

    public boolean empty() {
        return cache == null || cache.asMap().isEmpty();
    }

    public void put(String key, V val) {
        cache.put(key, val);
    }

    public void putAll(Map<? extends String, ? extends V> map) {
        cache.putAll(map);
    }
}