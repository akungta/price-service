package com.akashrungta.priceservice.models;

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

    int instrumentId;
    LocalDateTime asOf;
    Payload payload;

}
