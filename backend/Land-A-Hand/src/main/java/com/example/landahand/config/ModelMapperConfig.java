package com.example.landahand.config;

import com.example.landahand.model.*;
import com.example.landahand.model.dto.*;
import com.example.landahand.model.requests.AvailabilityRequest;
import org.modelmapper.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper dtoMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.addMappings(postDtoPropertyMap());
        modelMapper.addMappings(reviewDtoPropertyMap());
        modelMapper.addMappings(requestDtoPropertyMap());
        configureAvailabilityMapping(modelMapper);
        return modelMapper;
    }

    private PropertyMap<Task, TaskDto> postDtoPropertyMap() {
        return new PropertyMap<Task, TaskDto>() {
            @Override
            protected void configure() {
                map().setId(source.getId());
                map().setDescription(source.getDescription());
                map().setPrice(source.getPrice());
                map().setActivityTypeId(source.getTaskType().getId());
                using(taskPicturesToBase64Converter()).map(source.getPictures(), destination.getPicture()); // Convert to Base64
                map().setUserId(source.getUser().getId());
                map().setActivityTypeName(source.getTaskType().getType());
            }
        };
    }

    @Bean
    public Converter<List<TaskPicture>, List<String>> taskPicturesToBase64Converter() {
        return new AbstractConverter<List<TaskPicture>, List<String>>() {
            @Override
            protected List<String> convert(List<TaskPicture> source) {
                return source.stream()
                        .map(picture -> Base64.getEncoder().encodeToString(picture.getPicture()))
                        .collect(Collectors.toList());
            }
        };
    }

    private PropertyMap<Review, ReviewDto> reviewDtoPropertyMap() {
        return new PropertyMap<Review, ReviewDto>() {
            @Override
            protected void configure() {
                map().setId(source.getId());
                map().setComment(source.getComment());
                map().setRating(source.getRating());
                map().setUserId(source.getUser().getId());
                map().setPicture(source.getUser().getPicture());
                map().setPostId(source.getTask().getId());
            }
        };
    }

    private PropertyMap<Request, RequestDto> requestDtoPropertyMap() {
        return new PropertyMap<Request, RequestDto>() {
            @Override
            protected void configure() {
                map().setRequestId(source.getId());
                map().setAvailabilityId(source.getAvailability().getId());
                map().setUserPosterId(source.getUserPoster().getId());
                map().setUserRequesterId(source.getUserRequester().getId());
                map().setUserBio(source.getUserRequester().getBio());
                map().setStatus(source.getStatus());
                map().setPostId(source.getTask().getId());
                map().setAvailabilityTime("");
            }
        };
    }

    private void configureAvailabilityMapping(ModelMapper modelMapper) {
        modelMapper.createTypeMap(Availability.class, AvailabilityRequest.class)
                .addMappings(mapping -> {
                    mapping.using(localDateTimeToStringConverter())
                            .map(Availability::getDateTimeFrom, AvailabilityRequest::setDateTimeFrom);
                    mapping.using(localDateTimeToStringConverter())
                            .map(Availability::getDateTimeTo, AvailabilityRequest::setDateTimeTo);
                    mapping.map(Availability::getId, AvailabilityRequest::setId);
                });
    }

    @Bean
    public Converter<LocalDateTime, String> localDateTimeToStringConverter() {
        return new AbstractConverter<LocalDateTime, String>() {
            @Override
            protected String convert(LocalDateTime source) {
                return source != null ? source.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")) : null;
            }
        };
    }
}
