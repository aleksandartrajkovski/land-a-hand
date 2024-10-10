package com.example.landahand.service.impl;

import com.example.landahand.model.PeopleGroup;
import com.example.landahand.repository.PeopleGroupRepository;
import com.example.landahand.service.PeopleGroupService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PeopleGroupServiceImpl implements PeopleGroupService {

    private final PeopleGroupRepository peopleGroupRepository;

    public PeopleGroupServiceImpl(PeopleGroupRepository peopleGroupRepository) {
        this.peopleGroupRepository = peopleGroupRepository;
    }

    @Override
    public List<PeopleGroup> findAll() {
        return this.peopleGroupRepository.findAll();
    }

    @Override
    public Optional<PeopleGroup> findById(Long id) {
        return this.peopleGroupRepository.findById(id);
    }
}

