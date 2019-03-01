package com.akashrungta.priceservice.core;

import com.akashrungta.priceservice.models.Record;
import com.akashrungta.priceservice.models.payloads.Stocks;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class RecordsManagerTest {

    public RecordsManager recordsManager = RecordsManager.getInstance();

    @Test
    void prepareAndCommit() {
        recordsManager.prepare("test", Lists.newArrayList(new Record("stocks", LocalDateTime.now(), new Stocks("ING", BigDecimal.valueOf(100)))));
        recordsManager.commit("test");
        assertEquals(Optional.of(BigDecimal.valueOf(100)), recordsManager.getPrice("stocks"));
    }

    @Test
    void prepareAndRollback() {
        recordsManager.prepare("test", Lists.newArrayList(new Record("stocks", LocalDateTime.now(), new Stocks("ING", BigDecimal.valueOf(100)))));
        recordsManager.rollback("test");
        assertEquals(Optional.empty(), recordsManager.getPrice("stocks"));
    }
}