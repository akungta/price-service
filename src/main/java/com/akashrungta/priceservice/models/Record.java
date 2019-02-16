package com.akashrungta.priceservice.models;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Record {

    int instrumentId;
    LocalDateTime asOf;
    Payload payload;

}
