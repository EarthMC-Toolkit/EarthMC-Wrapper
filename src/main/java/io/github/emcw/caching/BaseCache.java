package io.github.emcw.caching;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.emcw.exceptions.MissingEntryException;
import io.github.emcw.utils.Funcs;
import lombok.AccessLevel;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.TreeMap;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import java.util.stream.Collectors;

@SuppressWarnings({"unused", "BooleanMethodIsAlwaysInverted"})
public abstract class BaseCache<V> {
    @Setter(AccessLevel.PROTECTED) protected Cache<String, V> cache;
    protected final CacheOptions options;

    final Caffeine<Object, Object> builder = Caffeine.newBuilder();
    final Integer CONCURRENCY = Runtime.getRuntime().availableProcessors();

    ScheduledExecutorService scheduler = null; // Calls the updater at a fixed rate.
    @Setter(AccessLevel.PROTECTED) protected Runnable updater = null;

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

    /**
     * Creates the {@link #cache}, with some pre-setup according to {@link #options}.
     * <br><br>
     * If the strategy is {@link CacheStrategy#LAZY}, we simply set the expiry after write and build.
     * <br><br>
     * If the strategy is {@link CacheStrategy#TIME_BASED} or {@link CacheStrategy#HYBRID}
     * we initialize a scheduler to update
     */
    protected void buildCache() {
        if (options.strategy == CacheStrategy.LAZY) {
            builder.expireAfterWrite(options.expiry, options.unit);
        } else {
            initUpdateScheduler();
        }

        setCache(builder.build());
    }

    private void initUpdateScheduler() {
        scheduler = Executors.newScheduledThreadPool(CONCURRENCY);
        scheduler.scheduleAtFixedRate(() -> {
            try { updater.run(); }
            catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        }, options.expiry, options.expiry, options.unit);
    }

    private void stopUpdateScheduler() {
        scheduler = null;
    }

    /**
     * Runs the {@link #updater} if the cache strategy matches in the given one.
     */
    protected void updateIf(CacheStrategy strategy) {
        if (options.strategy != strategy) return;
        updater.run();
    }

    public Map<String, V> get(String @NotNull ... keys) {
        Map<String, V> all = all();

        return Funcs.parallelStreamArr(keys)
            .filter(all::containsKey)
            .collect(Collectors.toMap(k -> k, all::get));
    }

    public V single(String key) throws MissingEntryException {
        updateIf(CacheStrategy.HYBRID);

        V val = cache.getIfPresent(key);
        if (val == null) {
            // Expired and lazy, update.
            updateIf(CacheStrategy.LAZY);

            val = cache.getIfPresent(key);
            if (val == null) throw new MissingEntryException("Could not find entry by key '" + key + "'");
        }

        return val;
    }

    /**
     * Gets all elements from this cache in a case-insensitive order.
     * <br>Some subclasses may override this to implement additional behaviour.
     */
    public Map<String, V> all() {
        return cacheAsMap();
    }

    private @NotNull Map<String, V> cacheAsMap() {
        Map<String, V> map = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        map.putAll(cache.asMap());

        return map;
    }

//    public boolean has(String key) {
//        var all = all();
//
//        if (cache.asMap().containsKey(key)) return true;
//        return all().get(key) != null;
//    }

    /**
     * Invalidates all cache entries that are not being loaded, effectively clearing it.
     */
    protected void clearCache() {
        cache.invalidateAll();
    }

    public boolean cacheIsEmpty() {
        return cache == null || cache.asMap().isEmpty();
    }

//    public void put(String key, V val) {
//        cache.put(key, val);
//    }
//
//    public void putAll(Map<? extends String, ? extends V> map) {
//        cache.putAll(map);
//    }

    public void tryExpireCache() {
        boolean timeBased = options.strategy.equals(CacheStrategy.TIME_BASED);
        if (!timeBased) return;

        // Only clear if lazy or hybrid.
        clearCache();
    }

    public void tryUpdateCache() {
        tryExpireCache();
        updateCache(false);
    }

    public void forceUpdateCache() {
        updateCache(true);
    }

    protected abstract void updateCache(Boolean force);
}