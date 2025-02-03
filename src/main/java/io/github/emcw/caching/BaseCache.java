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
    @Setter(AccessLevel.PROTECTED)
    private Cache<String, V> cache = Caffeine.newBuilder().build();

    // Instead of emptying the cache, we just mark that it "needs an update" on the next call.
    // If the cache is time based, this variable is ignored and an update always happens.
    @Setter(AccessLevel.PROTECTED) private boolean cacheExpired = false;

    private final CacheOptions options;

    /**
     * Responsible for updating or expiring the cache at a fixed rate.
     * @see #initCacheScheduler() initCacheScheduler
     */
    ScheduledExecutorService scheduler = null;

    // TODO: Make this customizable?
    final int CONCURRENCY = Runtime.getRuntime().availableProcessors();

    /**
     * Abstract class acting as a parent to other cache classes and holds a reference to a Caffeine cache.<br>
     * It provides the fundamental methods (getAll, getSingle & getMultiple) that children automatically inherit.<br><br>
     *
     * It also initializes the scheduler which updates or expires data based on the
     * {@link CacheStrategy} of the given {@code options}.
     * @param cacheOptions The options that this cache will be setup with.
     * @see io.github.emcw.caching.CacheOptions
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

        Runnable runnable;

        if (options.strategy == CacheStrategy.LAZY) {
            runnable = () -> {
                if (cacheExpired) return;

                //System.out.println("[EMCW - Scheduler] Marking cache as expired.");
                cacheExpired = true;
            };
        } else {
            runnable = () -> {
                try {
                    //System.out.println("[EMCW - Scheduler] Updating cache with fresh data.");
                    updateCache();
                }
                catch (Exception e) {
                    System.err.println("Error updating the cache!\n" + e.getMessage());
                }
            };
        }

        scheduler.scheduleAtFixedRate(runnable, options.expiry, options.expiry, options.unit);
    }

    /**
     * Returns a thread-safe view of this cache as a {@link Map}.<br><br>
     * Some subclasses may override this to implement additional behaviour.
     */
    public Map<String, V> getAll() {
        updateCacheIfExpired();
        return cache.asMap();
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
        Map<String, V> all = getAll();
        return Stream.of(keys)
            .filter(all::containsKey)
            .map(all::get)
            .collect(Collectors.toList());
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
        updateCacheIfExpired();
        return cache.getIfPresent(key);
    }

    /**
     * Reports whether the cache contains no entries.
     * @return {@code true} if the underlying map contains no key-value mappings
     */
    public boolean cacheIsEmpty() {
        return cache.asMap().isEmpty();
    }

    /**
     * Will only update the cache if it was marked as "expired", which should only be the case
     * for strategies that update the cache on the next call after expiry, aka {@link CacheStrategy#LAZY LAZY}.<br><br>
     *
     * Calling this method on a {@link CacheStrategy#TIME_BASED TIME_BASED} cache will have no effect.
     */
    void updateCacheIfExpired() {
        if (!cacheExpired) return;

        cacheExpired = false;
        updateCache();

        //System.out.println("[EMCW] Updated expired cache..");
    }

    /**
     * Always updates the cache, regardless of whether it has expired.<br><br>
     *
     * If this cache is {@link CacheStrategy#TIME_BASED TIME_BASED}, this method is called
     * automatically at the interval defined in {@link #options} which was set when it was built.
     * @see BaseCache#BaseCache(CacheOptions)
     */
    public void updateCache() {
        Map<String, V> data = fetchCacheData();

        // Make sure we're using valid data to populate the cache with.
        if (data == null) return;
        if (data.isEmpty()) return;

        this.cache.putAll(data);
    }

    protected abstract Map<String, V> fetchCacheData();
}