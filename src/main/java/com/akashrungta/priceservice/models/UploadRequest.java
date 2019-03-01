package com.akashrungta.priceservice.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class UploadRequest {

    List<Record> records;

}
