package io.github.emcw.squaremap.api;

import io.github.emcw.caching.BaseCache;
import io.github.emcw.caching.CacheOptions;

import io.github.emcw.squaremap.SquaremapParser;
import io.github.emcw.squaremap.entities.SquaremapNation;

import io.github.emcw.interfaces.ILocatable;

import java.util.Map;

public class Nations extends BaseCache<SquaremapNation> implements ILocatable<SquaremapNation> {
    private final SquaremapParser parser;

    public Nations(SquaremapParser parser, CacheOptions options) {
        super(options);
        this.parser = parser;
    }

    @Override
    protected Map<String, SquaremapNation> fetchCacheData() {
        try {
            this.parser.parseMapData(true, true, false);
        } catch (Exception e) {
            System.err.println("[EMCW - Nations] Error fetching cache data:\n  " + e.getMessage());
            return null;
        }

        return this.parser.getNations();
    }
}