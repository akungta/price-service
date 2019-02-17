package com.akashrungta.priceservice.models.payloads;

import com.akashrungta.priceservice.models.Payload;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.dropwizard.jackson.JsonSnakeCase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonSnakeCase
@JsonTypeName("stocks")
public class Stocks implements Payload {

    String details;
    BigDecimal stockPrice;

    @Override
    public BigDecimal getPrice() {
        return this.stockPrice;
    }
}
