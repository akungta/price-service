package com.akashrungta.priceservice;

import lombok.Value;

@Value
public class SessionRecord {
    String sessionId;
    String instrumentId;
    AsOfPrice asOfPrice;
}
