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
@JsonTypeName("bonds")
public class Bonds implements Payload {

    BondDetails bondDetails;

    @Override
    public BigDecimal getPrice() {
        return bondDetails.getPrice();
    }

    @Data
    @AllArgsConstructor
    private static class BondDetails {
        BigDecimal price;
    }
}
