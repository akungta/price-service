package com.akashrungta.priceservice.models;

import com.akashrungta.priceservice.core.RecordDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@JsonDeserialize(using = RecordDeserializer.class)
public class Record {

    int instrumentId;
    LocalDateTime asOf;
    Payload payload;

}
