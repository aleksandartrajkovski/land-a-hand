package com.example.landahand.service;

import com.example.landahand.model.TaskType;

import java.util.List;
import java.util.Optional;

public interface ActivityTypeService {

    List<TaskType> findAll();

    Optional<TaskType> findById(Long id);
}
