package com.example.landahand.service.impl;

import com.example.landahand.model.TaskType;
import com.example.landahand.repository.TaskTypeRepository;
import com.example.landahand.service.ActivityTypeService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ActivityTypeServiceImpl implements ActivityTypeService {

    private final TaskTypeRepository taskTypeRepository;

    public ActivityTypeServiceImpl(TaskTypeRepository taskTypeRepository) {
        this.taskTypeRepository = taskTypeRepository;
    }

    @Override
    public List<TaskType> findAll() {
        return this.taskTypeRepository.findAll();
    }

    @Override
    public Optional<TaskType> findById(Long id) {
        return this.taskTypeRepository.findById(id);
    }
}
