package com.example.landahand.service;

import com.example.landahand.model.Request;

import java.util.List;
import java.util.Optional;

public interface RequestService {

    List<Request> findAll();
    Optional<Request> findById(Long id);
    void acceptRequest(Long requestId);
    Request create(Request request, Long availabilityId, Long userId );
    List<Request> getRequestsByUserId(Long userId);
    public void declineRequest(Long requestId);
}
