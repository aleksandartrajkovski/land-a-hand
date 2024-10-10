package com.example.landahand.model.requests;

import lombok.Data;

@Data
public class AvailabilityRequest {
    public Long id;
    public String dateTimeFrom;
    public String dateTimeTo;
}
