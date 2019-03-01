package com.akashrungta.priceservice.core;


import com.akashrungta.priceservice.models.AsOfPrice;
import com.akashrungta.priceservice.models.SessionInstrumentAsOf;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingDeque;
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

    public final BlockingQueue<SessionInstrumentAsOf> queue = new LinkedBlockingDeque<>(1000);

    private final ConcurrentMap<String, AsOfPrice> latestPrices = new ConcurrentHashMap<>();

    private final ConcurrentMap<String, Map<String, AsOfPrice>> sessionLatestPrices = new ConcurrentHashMap<>();

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

    public void start(String sessionId) {
        sessionLatestPrices.put(sessionId, new HashMap<>());
    }


    public void add(SessionInstrumentAsOf record){
        sessionLatestPrices.computeIfPresent(record.getSessionId(),
                (sessionId, existingMap) -> {
                    existingMap.merge(record.getSessionId(), record.getAsOfPrice(), latestAsOfPriceFunction());
                    return existingMap;
                });
    }

    /**
     * Move the price data to complete map, which means session is completed, and data can be fetched
     */
    public void complete(String sessionId) {
        sessionLatestPrices.remove(sessionId).forEach(mergeInto(latestPrices));
    }

    /**
     * Discard the data for the session, because it was indicated to cancel the batch
     */
    public void cancel(String sessionId) {
        sessionLatestPrices.remove(sessionId);
    }

    public Optional<BigDecimal> getPrice(String instrumentId) {
        return Optional.ofNullable(latestPrices.getOrDefault(instrumentId, AsOfPrice.EMPTY).getPrice());
    }

}
