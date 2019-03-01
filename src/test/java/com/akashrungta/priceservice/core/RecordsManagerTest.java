package com.akashrungta.priceservice.core;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RecordsManagerTest {

    public RecordsManager recordsManager = RecordsManager.getInstance();

    @Test
    void prepareAndCommit() {
        //recordsManager.upload("test", Lists.newArrayList(new Record("stocks", LocalDateTime.now(), new Stocks("ING", BigDecimal.valueOf(100)))));
        recordsManager.complete("test");
        assertEquals(Optional.of(BigDecimal.valueOf(100)), recordsManager.getPrice("stocks"));
    }

    @Test
    void prepareAndRollback() {
        //recordsManager.upload("test", Lists.newArrayList(new Record("stocks", LocalDateTime.now(), new Stocks("ING", BigDecimal.valueOf(100)))));
        recordsManager.cancel("test");
        assertEquals(Optional.empty(), recordsManager.getPrice("stocks"));
    }
}