package io.github.emcw.caching;

import com.github.benmanes.caffeine.cache.Cache;

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

    final int CONCURRENCY = Runtime.getRuntime().availableProcessors();

    /**
     * Responsible for updating or expiring the cache at a fixed rate.
     * @see #initCacheScheduler() initCacheScheduler
     */
    ScheduledExecutorService scheduler = null;

    /**
     * Abstract class acting as a parent to other cache classes and holds a reference to a Caffeine cache.<br>
     * It provides the fundamental methods (getAll, getSingle & getMultiple) that children automatically inherit.<br><br>
     *
     * It also initializes the scheduler which updates or expires data based on the
     * {@link CacheStrategy} of the given {@code options}.
     * @param cacheOptions The options that this cache will be setup with.
     * @see  io.github.emcw.caching.CacheOptions
     */
    public BaseCache(CacheOptions cacheOptions) {
        options = cacheOptions;
        initCacheScheduler();
    }

    /**
     * Creates the {@link #cache} after initializing a new scheduler that behaves
     * differently according to the strategy in {@link #options}.
     * <br><br>
     * If the strategy is {@link CacheStrategy#LAZY}, we simply set the cache back to null to "expire" it.
     * <br><br>
     * If the strategy is {@link CacheStrategy#TIME_BASED}, the cache is always updated with fresh data.
     */
    void initCacheScheduler() {
        // Initialize a scheduler that will cache update method we specified.
        scheduler = Executors.newScheduledThreadPool(CONCURRENCY);

        if (options.strategy == CacheStrategy.LAZY) {
            scheduler.scheduleAtFixedRate(() -> {
                //if (!cacheExpired()) return;

                System.out.println("Expiring cache.");
                setCache(null);
            }, options.expiry, options.expiry, options.unit);
        } else {
            scheduler.scheduleAtFixedRate(() -> {
                try {
                    System.out.println("Updating cache.");
                    forceUpdateCache();
                }
                catch (Exception e) {
                    throw new RuntimeException(e.getMessage());
                }
            }, options.expiry, options.expiry, options.unit);
        }
    }

    /**
     * Attempts to get a single element from the cache.
     * Returns {@code null} if no such element exists with the given key.
     *
     * <br><br>
     * Some subclasses may override this to implement additional behaviour.
     */
    @Nullable
    public V getSingle(String key) {
        tryUpdateCache();
        return cache.getIfPresent(key);
    }

    /**
     * Returns a new list containing only the values of the specified keys by calling {@link #getAll} and
     * filtering out any that aren't in {@code keys}, then getting the values of those left as a list.
     * <br><br>
     * For a small amount of keys, calling {@link #getSingle} for each one may be quicker than filtering.
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

    /**
     * Should only be true in these cases:<br><br>
     * - The cache was built with a lazy strategy.<br>
     * - We have not made any calls that try to update the cache after it expired.
     * @return true if cache is null (expired).
     */
    public boolean cacheExpired() {
        return cache == null;
    }

//    /**
//     * Sets the cache back to {@code null} if the caching strategy (see {@link BaseCache#options}) is not
//     * {@link CacheStrategy#TIME_BASED}. Therefore it is {@link CacheStrategy#LAZY} or some sort of hybrid that relies on expiry.
//     */
//    protected void expireCacheIfLazy() {
//        // The scheduler should take care of updating in this case.
//        if (options.strategy.equals(CacheStrategy.TIME_BASED)) return;
//        setCache(null);
//    }

    /**
     * Will try to update the cache if it is "expired", which should only be the case
     * for strategies that update the cache on the next call after expiry, aka lazy.
     */
    public void tryUpdateCache() {
        updateCache(cacheExpired());
    }

    /**
     * Always updates the cache, regardless of whether it has expired.
     * This is used for time based strategies that demand an update every interval.
     */
    public void forceUpdateCache() {
        updateCache(true);
    }

    protected abstract void updateCache(Boolean force);
}