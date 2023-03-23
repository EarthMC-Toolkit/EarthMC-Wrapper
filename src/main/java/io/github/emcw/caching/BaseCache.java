package io.github.emcw.caching;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.AccessLevel;
import lombok.Setter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

public abstract class BaseCache<V> {
    @Setter(AccessLevel.PRIVATE) public Cache<String, V> cache;

    @Setter Duration expiry;
    @Setter Integer maxSize;

    /**
    * This class acts as a parent to all other Cache classes.
    * It provides common settings and methods used to build a new instance.
    * @param expiryTime The duration at which to remove all entries at.
    * @param maxEntries The max number of entries this cache can hold before evicting.
    */
    protected BaseCache(Duration expiryTime, Integer maxEntries) {
        setExpiry(expiryTime);
        setMaxSize(maxEntries);

        cache = init();
    }

    public Map<String, V> get(String... keys) {
        return cache.getAllPresent(Arrays.stream(keys).parallel().toList());
    }

    @Nullable
    public V single(String key) {
        return cache.getIfPresent(key);
    }

    public Map<String, V> all() {
        return cache.asMap();
    }

    @Contract(" -> new")
    private @NotNull Cache<String, V> init() {
        Caffeine<Object, Object> builder = Caffeine.newBuilder();

        if (!expiry.isZero()) builder.expireAfterWrite(expiry);
        if (maxSize != 0) builder.maximumSize(maxSize);

        return builder.build();
    }
}
