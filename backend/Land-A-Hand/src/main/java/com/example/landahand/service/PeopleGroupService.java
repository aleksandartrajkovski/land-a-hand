package com.example.landahand.service;

import com.example.landahand.model.PeopleGroup;

import java.util.List;
import java.util.Optional;

public interface PeopleGroupService {

    List<PeopleGroup> findAll();

    Optional<PeopleGroup> findById(Long id);
}
