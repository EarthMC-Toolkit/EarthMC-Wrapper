package io.github.emcw.caching;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import lombok.AccessLevel;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings({"unused", "BooleanMethodIsAlwaysInverted"})
public abstract class BaseCache<V> {
    @Setter(AccessLevel.PROTECTED) protected Cache<String, V> cache;
    protected final CacheOptions options;

    final Caffeine<Object, Object> builder = Caffeine.newBuilder();
    final Integer CONCURRENCY = Runtime.getRuntime().availableProcessors();

    ScheduledExecutorService scheduler = null; // Calls the updater at a fixed rate.

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
     * If the strategy is {@link CacheStrategy#TIME_BASED} we initialize a scheduler to update instead.
     */
    protected void buildCache() {
        if (options.strategy == CacheStrategy.LAZY) {
            builder.expireAfterWrite(options.expiry, options.unit);
        } else {
            // Initialize a scheduler that will cache update method we specified.
            scheduler = Executors.newScheduledThreadPool(CONCURRENCY);
            scheduler.scheduleAtFixedRate(() -> {
                try {
                    forceUpdateCache();
                }
                catch (Exception e) {
                    throw new RuntimeException(e.getMessage());
                }
            }, options.expiry, options.expiry, options.unit);
        }

        setCache(builder.build());
    }

    @Nullable
    public V getSingle(String key) {
        tryUpdateCache();
        return cache.getIfPresent(key);
    }

    /**
     * Returns a new list containing only the values of the specified keys by calling `getAll()`,
     * filtering out any that aren't in {@code keys} and getting the values of those left as a list.
     * <br><br>
     * For a small amount of keys, calling `getSingle` for each one may be quicker than filtering.
     *
     * <br><br>
     * Some subclasses may override this to implement additional behaviour.
     */
    public List<V> getMultiple(String @NotNull ... keys) {
        tryUpdateCache();
        
        Map<String, V> all = getAll();
        return Stream.of(keys)
            .filter(all::containsKey)
            .map(all::get)
            .collect(Collectors.toList());
    }

    /**
     * Returns a thread-safe view of this cache as a {@link Map}.<br><br>
     * Some subclasses may override this to implement additional behaviour.
     */
    public Map<String, V> getAll() {
        tryUpdateCache();
        return cache.asMap();
    }

//    /**
//     * Gets all elements from this cache where null keys are not permitted and are case-insensitive.<br>
//     */
//    private @NotNull Map<String, V> cacheAsCaseInsensitiveMap() {
//        Map<String, V> map = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
//        map.putAll(cache.asMap());
//
//        return map;
//    }

    public boolean cacheIsEmpty() {
        return cache == null || cache.asMap().isEmpty();
    }

    /**
     * Invalidates all cache entries that are not being loaded, effectively clearing it.
     */
    protected void clearCache() {
        cache.invalidateAll();
    }

    // NOTE: Clearing might be redundant if we always do setCache in updateCache anyway.
    //       This method would only matter if we were *putting* data, not overwriting.
    protected void clearCacheIfLazy() {
        boolean lazy = options.strategy.equals(CacheStrategy.LAZY);
        if (!lazy) return;

        clearCache();
    }

    public void tryUpdateCache() {
        updateCache(false);
    }

    public void forceUpdateCache() {
        updateCache(true);
    }

    protected abstract void updateCache(Boolean force);
}