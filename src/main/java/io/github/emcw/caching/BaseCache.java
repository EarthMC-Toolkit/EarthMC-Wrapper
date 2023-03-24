package io.github.emcw.caching;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.emcw.core.EMCMap;
import lombok.AccessLevel;
import lombok.Setter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.Arrays;
import java.util.Map;

public class BaseCache<V> {
    @Setter(AccessLevel.PRIVATE) public Cache<String, V> cache;

    @Setter Duration expiry;
    @Setter Integer maxSize;

    /**
    * Abstract class acting as a parent to other cache classes and holds a reference to a Caffeine cache.<br>
    * It provides the fundamental methods (get, single & all) that children automatically inherit.
    *
    * @param expiryTime The duration at which to remove all entries at.
    * @param maxEntries The max number of entries this cache can hold before evicting.
    */
    protected BaseCache(Duration expiryTime, Integer maxEntries) {
        init(expiryTime, maxEntries);
    }

    public BaseCache(Duration expiryTime) {
        init(expiryTime, null);
    }

    public BaseCache() {
        init(Duration.ofMinutes(3), null);
    }

    private void init(Duration expiryTime, Integer maxEntries) {
        setExpiry(expiryTime);
        setMaxSize(maxEntries);

        cache = setupCache();
    }

    public Map<String, V> get(String... keys) {
        return cache.getAllPresent(Arrays.asList(keys));
    }

    @Nullable
    public V single(String key) {
        return cache.getIfPresent(key);
    }

    public Map<String, V> all() {
        return cache.asMap();
    }

    @Contract(" -> new")
    private @NotNull Cache<String, V> setupCache() {
        Caffeine<Object, Object> builder = Caffeine.newBuilder();

        if (!expiry.isZero()) builder.expireAfterWrite(expiry);
        if (maxSize != null) builder.maximumSize(maxSize);

        return builder.build();
    }
}
