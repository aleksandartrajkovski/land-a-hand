package com.example.landahand.repository;

import com.example.landahand.model.PeopleGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PeopleGroupRepository extends JpaRepository<PeopleGroup, Long> {
}
