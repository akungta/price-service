package com.akashrungta.priceservice.core;

import io.dropwizard.lifecycle.Managed;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JobExecutionService implements Managed {

    private ExecutorService executor = Executors.newFixedThreadPool(4);

    @Override
    public void start() throws Exception {
        executor.execute(new RecordsConsumer(RecordsManager.getInstance()));
    }

    @Override
    public void stop() throws Exception {
        executor.shutdown();
    }
}
