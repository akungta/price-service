package com.akashrungta.priceservice.core;


import com.akashrungta.priceservice.AsOfPrice;
import com.akashrungta.priceservice.SessionRecord;
import com.akashrungta.priceservice.models.Record;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class RecordsManager {

    private RecordsManager() {
    }

    private static class Holder {
        private static final RecordsManager INSTANCE = new RecordsManager();
    }

    public static RecordsManager getInstance() {
        return Holder.INSTANCE;
    }

    public final BlockingQueue<SessionRecord> uploadQueue = new LinkedBlockingDeque<>(1000);

    public final ExecutorService executor = Executors.newFixedThreadPool(4);

    private final ConcurrentMap<String, AsOfPrice> latestPrices = new ConcurrentHashMap<>();

    private final Map<String, ConcurrentMap<String, AsOfPrice>> sessionLatestPrices = new HashMap<>();

    /**
     * This method returns a bifunction which takes two asOfPrice, and returns the one which has
     * latest asOf datetime
     */
    private BiFunction<AsOfPrice, AsOfPrice, AsOfPrice> latestAsOfPriceFunction() {
        return (oldValue, newValue) -> {
            if (newValue.getAsOf().isAfter(oldValue.getAsOf())) {
                return newValue;
            } else {
                return oldValue;
            }
        };
    }

    private BiConsumer<String, AsOfPrice> mergeInto(ConcurrentMap<String, AsOfPrice> result){
        return (instrumentId, asOfPrice) -> result.merge(instrumentId, asOfPrice, latestAsOfPriceFunction());
    }

    public void prepare(String sessionId) {
        sessionLatestPrices.put(sessionId, new ConcurrentHashMap<>());
    }

    /**
     * Takes a list of records, and mergeInto the record into existing uncommitted map. Merge happens for each
     * session_id and as well as instrument. Retaining the latest asOf price.
     */
    public void upload(String sessionId, List<Record> records) {
        records.forEach(record -> uploadQueue.offer(new SessionRecord(
                sessionId,
                record.getInstrumentId(),
                new AsOfPrice(record.getAsOf(), record.getPayload().getPrice())))
        );
    }

    private void consumeRecords(SessionRecord record){
        sessionLatestPrices.merge(record.getSessionId(), getAsOfPricesOf(record),
                (existingMap, newMap) -> {
                    newMap.forEach(mergeInto(existingMap));
                    return existingMap;
                });
    }

    /**
     * This method takes list of records from the /upload operation, and returns a temporary map
     */
    private ConcurrentMap<String, AsOfPrice> getAsOfPricesOf(SessionRecord record) {
        ConcurrentMap<String, AsOfPrice> retval = new ConcurrentHashMap<>();
        retval.put(record.getInstrumentId(), record.getAsOfPrice());
        return retval;
    }

    /**
     * Move the price data to complete map, which means batch is completed, and data can be fetched
     */
    public void complete(String sessionId) {
        sessionLatestPrices.remove(sessionId).forEach(mergeInto(latestPrices));
    }

    /**
     * Discard the nonCommitted data for session_id, because session_id indicated to cancel the batch
     */
    public void cancel(String session_id) {
        sessionLatestPrices.remove(session_id);
    }

    public Optional<BigDecimal> getPrice(String instrumentId) {
        return Optional.ofNullable(latestPrices.getOrDefault(instrumentId, AsOfPrice.EMPTY).getPrice());
    }

}
