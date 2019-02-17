package com.akashrungta.priceservice.core;


import com.akashrungta.priceservice.models.Record;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class IncompleteRecordsManager {

    private IncompleteRecordsManager(){ }

    private static class Holder {
        private static final IncompleteRecordsManager INSTANCE = new IncompleteRecordsManager();
    }

    public static IncompleteRecordsManager getInstance() {
        return Holder.INSTANCE;
    }

    private ConcurrentMap<String, List<Record>> providerRecords = new ConcurrentHashMap<>();

    public void addAll(String provider, List<Record> records) {
        providerRecords.merge(provider, records, (records1, records2) -> {
            records1.addAll(records2);
            return records1;
        });
    }

    public List<Record> cleanup(String provider) {
        return providerRecords.remove(provider);
    }

}
