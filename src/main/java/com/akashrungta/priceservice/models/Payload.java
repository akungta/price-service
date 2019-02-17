package com.akashrungta.priceservice.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;

public interface Payload {

    @JsonIgnore
    BigDecimal getPrice();

}
