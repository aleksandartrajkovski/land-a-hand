package com.example.landahand.model.dto;

import com.example.landahand.model.Availability;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AvailabilityParser {

    public static Availability parseAvailability(String selectedAvailabilityFrom, String selectedAvailabilityTo) {
        // Define the date format with hours, minutes, and seconds
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        // Parse the start and end datetime strings
        LocalDateTime startDateTime = LocalDateTime.parse(selectedAvailabilityFrom, formatter);
        LocalDateTime endDateTime = LocalDateTime.parse(selectedAvailabilityTo, formatter);

        // Create and return an Availability object with the parsed datetime
        return new Availability(startDateTime, endDateTime);
    }
}
