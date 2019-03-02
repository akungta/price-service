package com.akashrungta.priceservice.core;

import com.akashrungta.priceservice.models.AsOfPrice;
import com.akashrungta.priceservice.models.SessionInstrumentAsOf;
import com.akashrungta.priceservice.models.enums.Status;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RecordsManagerTest {

    public RecordsManager recordsManager = RecordsManager.getInstance();

    @Test
    void prepareAndCommit() throws InterruptedException {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new RecordsConsumer());
        recordsManager.start("test");
        recordsManager.add(new SessionInstrumentAsOf("test", Status.ADD, "stocks", new AsOfPrice(LocalDateTime.now(), BigDecimal.valueOf(100))));
        recordsManager.complete("test");
        Thread.sleep(1000);
        assertEquals(Optional.of(BigDecimal.valueOf(100)), recordsManager.getPrice("stocks"));
    }

    @Test
    void prepareAndRollback() throws InterruptedException {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new RecordsConsumer());
        recordsManager.start("test");
        recordsManager.add(new SessionInstrumentAsOf("test", Status.ADD, "stocks", new AsOfPrice(LocalDateTime.now(), BigDecimal.valueOf(100))));
        recordsManager.cancel("test");
        Thread.sleep(1000);
        assertEquals(Optional.empty(), recordsManager.getPrice("stocks"));
    }
}