package com.akashrungta.priceservice.models;

import io.dropwizard.jackson.JsonSnakeCase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Value;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@JsonSnakeCase
public class LatestPrice {

    Integer instrumentId;

    BigDecimal price;

}
