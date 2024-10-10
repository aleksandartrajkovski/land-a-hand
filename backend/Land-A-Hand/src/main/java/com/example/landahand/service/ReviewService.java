package com.example.landahand.service;

import com.example.landahand.model.dto.ReviewDto;
import com.example.landahand.model.requests.ReviewRequest;

import java.util.List;
import java.util.Optional;

public interface ReviewService {
    List<ReviewDto> findAll();

    Optional<ReviewDto> findById(Long id);

    ReviewDto create(ReviewRequest reviewRequest);

    void delete(Long id);

    ReviewDto update(Long id, ReviewRequest reviewRequest);
}
