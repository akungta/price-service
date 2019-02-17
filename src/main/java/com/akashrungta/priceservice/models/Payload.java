package com.akashrungta.priceservice.models;

import com.akashrungta.priceservice.models.payloads.Bonds;
import com.akashrungta.priceservice.models.payloads.Stocks;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.math.BigDecimal;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Stocks.class, name = "stocks"),
        @JsonSubTypes.Type(value = Bonds.class, name = "bonds")
})
public abstract class Payload {

    @JsonIgnore
    public abstract BigDecimal getPrice();

}
