package com.example.landahand.repository;

import com.example.landahand.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query("SELECT DISTINCT t.city FROM Task t WHERE t.city IS NOT NULL")
    List<String> findDistinctCities();  // Custom query to get distinct city names

    List<Task> findByCity(String city);
    List<Task> findAllByTaskTypeId(Long activityTypeId);
    List<Task> findAllByUserId(Long userId);
    List<Task> findByCityAndTaskTypeId(String city, Long activityTypeId);
}
