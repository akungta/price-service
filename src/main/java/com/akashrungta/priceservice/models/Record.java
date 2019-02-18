package com.akashrungta.priceservice.models;

import com.akashrungta.priceservice.models.payloads.Bonds;
import com.akashrungta.priceservice.models.payloads.Stocks;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.dropwizard.jackson.JsonSnakeCase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonSnakeCase
public class Record {

    String instrumentId;

    LocalDateTime asOf;

    /**
     * Payload has been configured to use Jackson Polymorphism to deserialize the payload json data into
     * object of specific subclass, based on the value indicated by property "instrumentId"
     * Example: if instrument_id="stocks", Payload will object of Stocks.class
     */
    @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
            property = "instrument_id")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = Stocks.class, name = "stocks"),
            @JsonSubTypes.Type(value = Bonds.class, name = "bonds")
    })
    Payload payload;

}
