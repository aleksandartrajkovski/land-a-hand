package com.example.landahand.web;

import com.example.landahand.model.dto.TaskDto;
import com.example.landahand.model.dto.TaskWithReviewsDto;
import com.example.landahand.model.requests.TaskRequest;
import com.example.landahand.service.TaskService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/posts")
@CrossOrigin(origins = {"*"})
public class TaskController {

    private final TaskService taskService;
    private final static int PAGE_SIZE = 6;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/paginated")
    public ResponseEntity<Page<TaskDto>> getPaginatedPosts(
            @RequestParam(required = false) Long activityTypeId,
            @RequestParam(defaultValue = "0") int page
    ) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE);
        return ResponseEntity.ok().body(this.taskService.findAllPaginated(activityTypeId, pageable));
    }

    @GetMapping
    public ResponseEntity<List<TaskDto>> getPosts() {
        return ResponseEntity.ok().body(this.taskService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskWithReviewsDto> getPostById(@PathVariable Long id) {
        return this.taskService.findById(id).map(post -> ResponseEntity.ok().body(post))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/add")
    public ResponseEntity<TaskDto> createPost(
            @RequestPart("task") String taskRequestString,  // Accept `task` as a string
            @RequestPart("pictures") List<MultipartFile> pictures) {

        // Convert `taskRequestString` (JSON) to `TaskRequest`
        ObjectMapper objectMapper = new ObjectMapper();
        TaskRequest taskRequest;
        try {
            taskRequest = objectMapper.readValue(taskRequestString, TaskRequest.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse task request", e);
        }

        // Set pictures in the TaskRequest object
        taskRequest.setPictures(pictures);

        TaskDto newPost = this.taskService.create(taskRequest);
        return new ResponseEntity<>(newPost, HttpStatus.CREATED);
    }


    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TaskDto> updatePost(@PathVariable Long id,
                                              @RequestPart("task") String taskRequestString,  // Accept `task` as a string
                                              @RequestPart("pictures") List<MultipartFile> pictures) {

        // Convert the JSON taskRequestString into a TaskRequest object
        ObjectMapper objectMapper = new ObjectMapper();
        TaskRequest taskRequest;
        try {
            taskRequest = objectMapper.readValue(taskRequestString, TaskRequest.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse task request", e);
        }

        // Set pictures in the TaskRequest object
        taskRequest.setPictures(pictures);

        // Now call the service to update the task
        TaskDto updatedPost = this.taskService.update(id, taskRequest);
        return new ResponseEntity<>(updatedPost, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePost(@PathVariable Long id) {
        this.taskService.delete(id);
        return new ResponseEntity<>("Task deleted successfully!", HttpStatus.OK);
    }

    @GetMapping("/{postId}/user")
    public ResponseEntity<Long> getUserIdByPostId(@PathVariable Long postId) {
        Optional<Long> userId = taskService.findUserIdByPostId(postId);
        return userId.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/cities")
    public ResponseEntity<List<String>> getAllCities() {
        List<String> cities = taskService.getAllCities();
        return ResponseEntity.ok().body(cities);
    }

    @GetMapping("/filterByCity")
    public List<TaskDto> getTasksByCity(@RequestParam(value = "city", required = false) String city) {
        if (city == null || city.isEmpty()) {
            return taskService.findAll();  // If no city is selected, return all tasks
        }
        return taskService.findTasksByCity(city);  // Otherwise, return filtered tasks by city
    }

    @GetMapping("/filter")
    public List<TaskDto> getTasksByCityAndActivityType(
            @RequestParam(value = "city", required = false) String city,
            @RequestParam(value = "activityTypeId", required = false) Long activityTypeId) {

        System.out.println("Received city "+city);
        System.out.println("Received activityTypeId:" + activityTypeId);

        // If both city and activity type are provided, filter by both
        if (city != null && activityTypeId != null) {
            return taskService.findTasksByCityAndActivityType(city, activityTypeId);
        }
        // If only city is provided, filter by city
        else if (city != null) {
            return taskService.findTasksByCity(city);
        }
        // If only activity type is provided, filter by activity type
        else if (activityTypeId != null) {
            return taskService.findTasksByActivityType(activityTypeId);
        }
        // If neither is provided, return all tasks
        else {
            return taskService.findAll();
        }
    }
}

