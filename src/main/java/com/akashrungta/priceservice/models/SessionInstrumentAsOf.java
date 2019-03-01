package com.akashrungta.priceservice.models;

import com.akashrungta.priceservice.models.enums.Status;
import lombok.Value;

@Value
public class SessionInstrumentAsOf {
    String sessionId;
    Status status;
    String instrumentId;
    AsOfPrice asOfPrice;

    public SessionInstrumentAsOf(String sessionId, Status status) {
        this.sessionId = sessionId;
        this.status = status;
        this.asOfPrice = null;
        this.instrumentId = null;
    }

    public SessionInstrumentAsOf(String sessionId, Status status, String instrumentId, AsOfPrice asOfPrice) {
        this.sessionId = sessionId;
        this.status = status;
        this.instrumentId = instrumentId;
        this.asOfPrice = asOfPrice;
    }
}
