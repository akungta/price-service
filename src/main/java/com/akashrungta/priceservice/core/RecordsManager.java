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

    private final ConcurrentMap<String, Pair<LocalDateTime, BigDecimal>> committedPrices = new ConcurrentHashMap<>();

    private final ConcurrentMap<String, Map<String, Pair<LocalDateTime, BigDecimal>>> nonCommittedPrices = new ConcurrentHashMap<>();

    /**
     * This method takes list of records from the /upload operation, and returns a temporary map
     */
    private Map<String, Pair<LocalDateTime, BigDecimal>> getMap(List<Record> records) {
        Map<String, Pair<LocalDateTime, BigDecimal>> retval = new HashMap<>();
        records.forEach(r -> {
            retval.merge(r.getInstrumentId(), Pair.of(r.getAsOf(),r.getPayload().getPrice()), getPairBiFunction());
        });
        return retval;
    }

    /**
     * This method returns a bifunction which takes two Pair of <asOf, price>, and returns the one which has
     * latest asOf datetime
     */
    private BiFunction<Pair<LocalDateTime, BigDecimal>, Pair<LocalDateTime, BigDecimal>, Pair<LocalDateTime, BigDecimal>> getPairBiFunction() {
        return (p1, p2) -> {
            if (p2.getLeft().isAfter(p1.getLeft())) {
                return p2;
            } else {
                return p1;
            }
        };
    }

    /**
     * Takes a list of records, and merge the record into existing uncommitted map. Merge happens for each
     * session_id and as well as instrument. Retaining the latest asOf price.
     */
    public void prepare(String session_id, List<Record> records){
        nonCommittedPrices.merge(session_id, getMap(records), (m1,m2) -> {
            m2.entrySet().stream().forEach(
                    entry -> {
                        m1.merge(entry.getKey(), entry.getValue(), getPairBiFunction());
                    }
            );
            return m1;
        });
    }

    /**
     * Move the price data to commit map, which means batch is completed, and data can be fetched
     */
    public void commit(String session_id) {
        Map<String, Pair<LocalDateTime, BigDecimal>> commitPrices = nonCommittedPrices.remove(session_id);
        committedPrices.putAll(commitPrices);
    }

    /**
     * Discard the nonCommitted data for session_id, because session_id indicated to cancel the batch
     */
    public void rollback(String session_id) {
        nonCommittedPrices.remove(session_id);
    }

    public Optional<BigDecimal> getPrice(String instrumentId){
        return Optional.ofNullable(committedPrices.getOrDefault(instrumentId, Pair.of(null, null)).getRight());
    }

}
