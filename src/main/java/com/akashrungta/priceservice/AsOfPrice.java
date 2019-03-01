package com.akashrungta.priceservice;

import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Value
public class AsOfPrice {
    public static final AsOfPrice EMPTY = new AsOfPrice(null, null);
    LocalDateTime asOf;
    BigDecimal price;
}
