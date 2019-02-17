package com.akashrungta.priceservice.models;

import lombok.Value;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Value
public class Upload {

    List<Record> records;

}
