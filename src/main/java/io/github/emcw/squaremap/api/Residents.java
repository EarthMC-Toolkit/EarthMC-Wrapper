package io.github.emcw.squaremap.api;

import io.github.emcw.caching.BaseCache;
import io.github.emcw.caching.CacheOptions;
import io.github.emcw.squaremap.entities.SquaremapOnlinePlayer;
import io.github.emcw.squaremap.entities.SquaremapOnlineResident;
import io.github.emcw.squaremap.entities.SquaremapResident;

import io.github.emcw.squaremap.SquaremapParser;
import lombok.Setter;

import java.util.Map;
import java.util.stream.Collectors;

public class Residents extends BaseCache<SquaremapResident> {
    private final SquaremapParser parser;

    @Setter private Players players;

    public Residents(SquaremapParser parser, CacheOptions options) {
        super(options);
        this.parser = parser;
    }

    @Override
    protected Map<String, SquaremapResident> fetchCacheData() {
        this.parser.parseMapData(false, false, true);
        return this.parser.getResidents();
    }

    public Map<String, SquaremapOnlineResident> getOnline() {
        Map<String, SquaremapResident> allResidents = this.getAll();
        Map<String, SquaremapOnlinePlayer> onlineWithTown = this.players.getAll()
            .entrySet().stream()
            .filter(entry -> allResidents.containsKey(entry.getKey()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return onlineWithTown.entrySet().stream().collect(Collectors.toMap(
            Map.Entry::getKey, entry -> new SquaremapOnlineResident(entry.getValue(), allResidents.get(entry.getKey()))
        ));
    }
}