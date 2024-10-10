package com.example.landahand.model.dto;

import com.example.landahand.model.requests.AvailabilityRequest;
import lombok.Data;

import java.util.List;

@Data
public class TaskDto {
    public Long id;
    public String userName;
    public String userSurname;
    public String description;
    public double price;
    public Long activityTypeId;
    public String activityTypeName;
    public Long userId;
    public String user;
    private List<String> picture;
    public List<AvailabilityRequest> availabilities;
    private String importanceLevel;
    private String peopleGroup;
    public String city;
    private String title;
}
