package com.akashrungta.priceservice.core;


import com.akashrungta.priceservice.models.Record;
import org.apache.commons.lang3.tuple.Pair;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class RecordsManager {

    private RecordsManager(){ }

    private static class Holder {
        private static final RecordsManager INSTANCE = new RecordsManager();
    }

    public static RecordsManager getInstance() {
        return Holder.INSTANCE;
    }

    private final ConcurrentMap<Integer, Pair<LocalDateTime, BigDecimal>> instrumentIdToPrices = new ConcurrentHashMap<>();

    public void insertRecords(List<Record> records){
        records.forEach(r -> {
            instrumentIdToPrices.merge(r.getInstrumentId(), Pair.of(r.getAsOf(),r.getPayload().getPrice()), (p1,p2) -> {
                if(p2.getLeft().isAfter(p1.getLeft())){
                    return p2;
                } else {
                    return p1;
                }
            });
        });
    }

    public Optional<BigDecimal> getPrice(Integer instrumentId){
        return Optional.ofNullable(instrumentIdToPrices.getOrDefault(instrumentId, Pair.of(null, null)).getRight());
    }

}
