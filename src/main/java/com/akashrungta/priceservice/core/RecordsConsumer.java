package com.akashrungta.priceservice.core;

import com.akashrungta.priceservice.models.SessionInstrumentAsOf;

public class RecordsConsumer implements Runnable {

    private final RecordsManager recordsManager = RecordsManager.getInstance();

    @Override
    public void run() {
        try {
            while (true) {
                SessionInstrumentAsOf value = recordsManager.queue.take();
                switch (value.getStatus()) {
                    case START:
                        recordsManager.start(value.getSessionId());
                        break;
                    case COMPLETE:
                        recordsManager.complete(value.getSessionId());
                        break;
                    case CANCEL:
                        recordsManager.cancel(value.getSessionId());
                        break;
                    default:
                        recordsManager.add(value);
                }

            }
        } catch (InterruptedException e) {
        }
    }

}
