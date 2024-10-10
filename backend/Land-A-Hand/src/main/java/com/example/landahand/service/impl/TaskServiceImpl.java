package com.example.landahand.service.impl;

import com.example.landahand.model.enums.ImportanceLevel;
import jakarta.transaction.Transactional;
import com.example.landahand.model.*;
import com.example.landahand.model.dto.TaskDto;
import com.example.landahand.model.dto.TaskWithReviewsDto;
import com.example.landahand.model.dto.ReviewDto;
import com.example.landahand.model.exceptions.*;
import com.example.landahand.model.requests.AvailabilityRequest;
import com.example.landahand.model.requests.TaskRequest;
import com.example.landahand.repository.*;
import com.example.landahand.service.TaskService;
import org.hibernate.Hibernate;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final PeopleGroupRepository peopleGroupRepository;
    private final TaskTypeRepository taskTypeRepository;
    private final UserRepository userRepository;
    private final AvailabilityRepository availabilityRepository;
    private final ModelMapper modelMapper;
    private final ReviewRepository reviewRepository;
    private final RequestRepository requestRepository;

    public TaskServiceImpl(TaskRepository taskRepository, PeopleGroupRepository peopleGroupRepository, TaskTypeRepository taskTypeRepository, UserRepository userRepository, AvailabilityRepository availabilityRepository, ModelMapper modelMapper, ReviewRepository reviewRepository, RequestRepository requestRepository) {
        this.taskRepository = taskRepository;
        this.peopleGroupRepository = peopleGroupRepository;
        this.taskTypeRepository = taskTypeRepository;
        this.userRepository = userRepository;
        this.availabilityRepository = availabilityRepository;
        this.modelMapper = modelMapper;
        this.reviewRepository = reviewRepository;
        this.requestRepository = requestRepository;
    }

    @Transactional
    public TaskDto mapTaskToDto(Task task) {
        // Ensure pictures are loaded
        Hibernate.initialize(task.getPictures());

        // Use modelMapper to map basic fields
        TaskDto taskDto = modelMapper.map(task, TaskDto.class);

        taskDto.setUserName(task.getUser().getName());
        taskDto.setUserSurname(task.getUser().getSurname());

        // Manually set the importanceLevel from the enum to a string in TaskDto
        taskDto.setImportanceLevel(task.getImportanceLevel().name());  // Convert enum to string
        return taskDto;
    }

    public List<String> getAllCities(){
        return taskRepository.findDistinctCities();
    }

    @Override
    public List<TaskDto> findTasksByCity(String city)
    {
        return taskRepository.findByCity(city)
                .stream()
                .map(task -> modelMapper.map(task, TaskDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskDto> findAll() {
        List<TaskDto> taskDtos = this.taskRepository.findAll()
                .stream()
                .map(post -> modelMapper.map(post, TaskDto.class))
                .collect(Collectors.toList());

        for (TaskDto taskDto : taskDtos) {
            setAvailabilitiesToPostDto(taskDto.getId(), taskDto);
        }
        return taskDtos;
    }

    @Override
    public Optional<TaskWithReviewsDto> findById(Long id) {
        Task task = this.taskRepository.findById(id).orElseThrow(() -> new TaskNotFound(id));
        TaskDto taskDto = modelMapper.map(task, TaskDto.class);
        setAvailabilitiesToPostDto(id, taskDto);

        TaskWithReviewsDto taskWithReviewsDto = modelMapper.map(taskDto, TaskWithReviewsDto.class);
        List<ReviewDto> reviewDtos = reviewRepository.findAllByTaskId(taskDto.getId())
                .stream()
                .map(review -> modelMapper.map(review, ReviewDto.class))
                .collect(Collectors.toList());

        for (ReviewDto reviewDto : reviewDtos) {
            User user = this.userRepository.findById(reviewDto.getUserId())
                    .orElseThrow(() -> new UserNotFound(reviewDto.getUserId()));
            reviewDto.setUser(user.getName() + " " + user.getSurname());
        }

        taskWithReviewsDto.setReviews(reviewDtos);

        TaskType taskType = this.taskTypeRepository.findById(taskDto.getActivityTypeId())
                .orElseThrow(() -> new TaskTypeNotFound(taskDto.getActivityTypeId()));
        User user = this.userRepository.findById(taskDto.getUserId())
                .orElseThrow(() -> new UserNotFound(taskDto.getUserId()));

        taskWithReviewsDto.setActivityType(taskType.getType());
        taskWithReviewsDto.setUser(user.getName() + " " + user.getSurname());
        taskWithReviewsDto.setPicture(taskDto.getPicture());
        return Optional.of(taskWithReviewsDto);
    }

    @Override
    @Transactional
    public TaskDto create(TaskRequest taskRequest) {
        Task task = new Task();

        TaskType taskType = taskTypeRepository.findById(taskRequest.activityTypeId)
                .orElseThrow(() -> new TaskTypeNotFound(taskRequest.activityTypeId));

        User user = userRepository.findById(taskRequest.userId)
                .orElseThrow(() -> new UserNotFound(taskRequest.userId));

        ImportanceLevel importanceLevelEnum;
        try {
            importanceLevelEnum = ImportanceLevel.valueOf(taskRequest.petSize.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IncorrectImportanceLevel();
        }

        task.setDescription(taskRequest.description);
        task.setImportanceLevel(importanceLevelEnum);
        task.setPrice(taskRequest.price);
        task.setTaskType(taskType);
        task.setUser(user);
        task.setTitle(taskRequest.title);
        task.setCity(taskRequest.city);

        List<TaskPicture> taskPictures = new ArrayList<>();
        for (MultipartFile picture : taskRequest.getPictures()) {
            try {
                TaskPicture taskPicture = new TaskPicture(picture.getBytes(), task);
                taskPictures.add(taskPicture);
            } catch (IOException e) {
                throw new RuntimeException("Failed to read picture file", e);
            }
        }
        task.setPictures(taskPictures);

        processAvailabilities(task, taskRequest.availabilities);

        task = this.taskRepository.save(task);

        Hibernate.initialize(task.getPictures());

        TaskDto taskDto = modelMapper.map(task, TaskDto.class);
        taskDto.setImportanceLevel(importanceLevelEnum.toString());

        setAvailabilitiesToPostDto(taskDto.getId(), taskDto);

        return taskDto;
    }

    @Override
    @Transactional
    public TaskDto update(Long id, TaskRequest taskRequest) {

        Task task = this.taskRepository.findById(id).orElseThrow(() -> new TaskNotFound(id));

        TaskType taskType = taskTypeRepository.findById(taskRequest.activityTypeId)
                .orElseThrow(() -> new TaskTypeNotFound(taskRequest.activityTypeId));

        ImportanceLevel importanceLevelEnum;
        try {
            importanceLevelEnum = ImportanceLevel.valueOf(taskRequest.petSize.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IncorrectImportanceLevel();
        }
        task.setDescription(taskRequest.description);
        task.setImportanceLevel(importanceLevelEnum);
        task.setPrice(taskRequest.price);
        task.setTaskType(taskType);
        task.setTitle(taskRequest.title);
        task.setCity(taskRequest.city);

        processAvailabilities(task, taskRequest.availabilities);

        task = this.taskRepository.save(task);

        TaskDto taskDto = modelMapper.map(task, TaskDto.class);
        taskDto.setImportanceLevel(importanceLevelEnum.toString());

        setAvailabilitiesToPostDto(taskDto.getId(), taskDto);

        return taskDto;
    }


    @Override
    @Transactional
    public void delete(Long id) {
        Task task = this.taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFound(id));
        this.availabilityRepository.deleteByTaskId(id);
        this.reviewRepository.deleteByTaskId(id);
        this.requestRepository.deleteByTaskId(id);
        this.taskRepository.delete(task);
    }

    @Override
    public Page<TaskDto> findAllPaginated(Long activityTypeId, Pageable pageable) {
        List<Task> tasks;
        if (activityTypeId != null) {
            tasks = this.taskRepository.findAllByTaskTypeId(activityTypeId);
        } else tasks = this.taskRepository.findAll();

        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;

        List<TaskDto> taskDtos;
        int totalCount = tasks.size();

        if (tasks.size() < startItem) {
            taskDtos = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + pageSize, totalCount);
            List<Task> sublist = tasks.subList(startItem, toIndex);
            taskDtos = sublist.stream()
                    .map(post -> modelMapper.map(post, TaskDto.class))
                    .collect(Collectors.toList());
        }

        for (TaskDto taskDto : taskDtos) {
            taskDto.setUser(getPostUser(taskDto.getUserId()));
        }

        return new PageImpl<>(taskDtos, pageable, totalCount);
    }

    @Override
    public Optional<Long> findUserIdByPostId(Long postId) {
        Task task = taskRepository.findById(postId).orElseThrow(() -> new TaskNotFound(postId));
        return Optional.ofNullable(task.getUser().getId());
    }

    private String getPostUser(Long userId) {
        User user = this.userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFound(userId));
        return user.getName() + " " + user.getSurname();
    }

    private void setAvailabilitiesToPostDto(Long postId, TaskDto taskDto) {
        List<Availability> availabilities = this.availabilityRepository.findAllByTaskId(postId);
        taskDto.setAvailabilities(availabilities
                .stream()
                .map(availability -> modelMapper.map(availability, AvailabilityRequest.class))
                .collect(Collectors.toList())
        );
    }

    public void processAvailabilities(Task task, List<AvailabilityRequest> availabilityRequests) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        for (AvailabilityRequest availabilityRequest : availabilityRequests) {
            LocalDateTime dateTimeFrom = LocalDateTime.parse(availabilityRequest.getDateTimeFrom(), formatter);
            LocalDateTime dateTimeTo = LocalDateTime.parse(availabilityRequest.getDateTimeTo(), formatter);

            // Create a new Availability object
            Availability availability = new Availability(dateTimeFrom, dateTimeTo);
            availability.setTask(task);

            // Save the availability (or perform your custom logic)
            availabilityRepository.save(availability);
            System.out.println("Parsed dateTimeFrom: " + dateTimeFrom);
            System.out.println("Parsed dateTimeTo: " + dateTimeTo);
        }
    }

    @Override
    public List<TaskDto> findTasksByCityAndActivityType(String city, Long activityTypeId) {
        return taskRepository.findByCityAndTaskTypeId(city, activityTypeId)
                .stream()
                .map(task -> modelMapper.map(task, TaskDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskDto> findTasksByActivityType(Long activityTypeId) {
        return taskRepository.findAllByTaskTypeId(activityTypeId)
                .stream()
                .map(task -> modelMapper.map(task, TaskDto.class))
                .collect(Collectors.toList());
    }


}
