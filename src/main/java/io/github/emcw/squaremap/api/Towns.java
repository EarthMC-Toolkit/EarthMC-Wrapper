package io.github.emcw.squaremap.api;

import io.github.emcw.caching.BaseCache;
import io.github.emcw.caching.CacheOptions;
import io.github.emcw.squaremap.entities.SquaremapTown;

import io.github.emcw.interfaces.ILocatable;
import io.github.emcw.squaremap.SquaremapParser;

import java.util.Map;

public class Towns extends BaseCache<SquaremapTown> implements ILocatable<SquaremapTown> {
    private final SquaremapParser parser;

    public Towns(SquaremapParser parser, CacheOptions options) {
        super(options);
        this.parser = parser;
    }

    @Override
    protected Map<String, SquaremapTown> fetchCacheData() {
        try {
            this.parser.parseMapData(true, false, true);
        } catch (Exception e) {
            System.err.println("[EMCW - Towns] Error fetching cache data:\n  " + e.getMessage());
            return null;
        }

        return this.parser.getTowns();
    }
}