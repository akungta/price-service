package com.akashrungta.priceservice.core;


import com.akashrungta.priceservice.models.Record;
import org.apache.commons.lang3.tuple.Pair;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiFunction;

public class RecordsManager {

    private RecordsManager(){ }

    private static class Holder {
        private static final RecordsManager INSTANCE = new RecordsManager();
    }

    public static RecordsManager getInstance() {
        return Holder.INSTANCE;
    }

    private final ConcurrentMap<String, Pair<LocalDateTime, BigDecimal>> instrumentIdToPrices = new ConcurrentHashMap<>();

    private final ConcurrentMap<String, Map<String, Pair<LocalDateTime, BigDecimal>>> nonCommittedPrices = new ConcurrentHashMap<>();

    public void prepare(String provider, List<Record> records){
        nonCommittedPrices.merge(provider, getMap(records), (m1,m2) -> {
            m2.entrySet().stream().forEach(
                    entry -> {
                        m1.merge(entry.getKey(), entry.getValue(), getPairBiFunction());
                    }
            );
            return m1;
        });
    }

    private Map<String, Pair<LocalDateTime, BigDecimal>> getMap(List<Record> records) {
        Map<String, Pair<LocalDateTime, BigDecimal>> retval = new HashMap<>();
        records.forEach(r -> {
            retval.merge(r.getInstrumentId(), Pair.of(r.getAsOf(),r.getPayload().getPrice()), getPairBiFunction());
        });
        return retval;
    }

    private BiFunction<Pair<LocalDateTime, BigDecimal>, Pair<LocalDateTime, BigDecimal>, Pair<LocalDateTime, BigDecimal>> getPairBiFunction() {
        return (p1, p2) -> {
            if (p2.getLeft().isAfter(p1.getLeft())) {
                return p2;
            } else {
                return p1;
            }
        };
    }

    public void commit(String provider) {
        Map<String, Pair<LocalDateTime, BigDecimal>> commitPrices = nonCommittedPrices.remove(provider);
        instrumentIdToPrices.putAll(commitPrices);
    }

    public void rollback(String provider) {
        nonCommittedPrices.remove(provider);
    }

    public Optional<BigDecimal> getPrice(String instrumentId){
        return Optional.ofNullable(instrumentIdToPrices.getOrDefault(instrumentId, Pair.of(null, null)).getRight());
    }

}
