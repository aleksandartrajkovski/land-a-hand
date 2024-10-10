package com.example.landahand.model.dto;

import com.example.landahand.model.requests.AvailabilityRequest;
import lombok.Data;

import java.util.List;

@Data

public class TaskWithReviewsDto {
    public Long id;
    public String description;
    public double price;
    public Long activityTypeId;
    public String activityType;
    public Long userId;
    public String user;
    public String city;
    public List<AvailabilityRequest> availabilities;
    public List<ReviewDto> reviews;
    private List<String> picture;
    private String importanceLevel;
    private String peopleGroup;
    public String title;
}