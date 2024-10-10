package com.example.landahand.service.impl;

import com.example.landahand.model.Availability;
import com.example.landahand.model.Task;
import com.example.landahand.model.Request;
import com.example.landahand.model.User;
import com.example.landahand.model.exceptions.AvailabilityNotFoundException;
import com.example.landahand.model.exceptions.TaskNotFoundException;
import com.example.landahand.model.exceptions.RequestNotFound;
import com.example.landahand.model.exceptions.UserNotFound;
import com.example.landahand.repository.AvailabilityRepository;
import com.example.landahand.repository.TaskRepository;
import com.example.landahand.repository.RequestRepository;
import com.example.landahand.repository.UserRepository;
import com.example.landahand.service.EmailService;
import com.example.landahand.service.RequestService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final AvailabilityRepository availabilityRepository;
    private final EmailService emailService;

    public RequestServiceImpl(RequestRepository requestRepository, UserRepository userRepository,
                              TaskRepository taskRepository, AvailabilityRepository availabilityRepository, EmailService emailService) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.availabilityRepository = availabilityRepository;
        this.emailService = emailService;
    }

    @Override
    public List<Request> findAll() {
        return requestRepository.findAll();
    }

    @Override
    public Optional<Request> findById(Long id) {
        return requestRepository.findById(id);
    }

    @Override
    public Request create(Request request, Long availabilityId, Long userId ) {
        // Fetch task and userRequester based on their IDs

        Availability availability = this.availabilityRepository.findById(availabilityId)
                .orElseThrow(() -> new AvailabilityNotFoundException(availabilityId));
        Task task = taskRepository.findById(availability.getTask().getId())
                .orElseThrow(() -> new TaskNotFoundException(availability.getTask().getId()));
        User userRequester = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFound(userId));

        //request.setTask(task);
        request.setAvailability(availability);
        request.setTask(task);
        request.setUserRequester(userRequester);
        request.setUserPoster(task.getUser());
        request.setStatus(Request.RequestStatus.PENDING);

        // Save the request
        Request savedRequest =  requestRepository.save(request);

        String requesterName = userRequester.getName();
        String requesterEmail = userRequester.getEmail();
        String postTitle = task.getTitle();
        String userPosterEmail = task.getUser().getEmail();
        String userPhoneNumber = userRequester.getPhone();
        String userBiography = userRequester.getBio();

        emailService.sendRequestEmail(userPosterEmail,requesterName,requesterEmail,postTitle,userPhoneNumber,userBiography);

        return savedRequest;
    }

    @Override
    public void acceptRequest(Long requestId) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RequestNotFound(requestId));
        request.setStatus(Request.RequestStatus.ACCEPTED);
        requestRepository.save(request);
    }

    @Override
    public void declineRequest(Long requestId) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RequestNotFound(requestId));
        request.setStatus(Request.RequestStatus.DECLINED);
        requestRepository.save(request);
    }

    @Override
    public List<Request> getRequestsByUserId(Long userId) {
        return requestRepository.findByUserPosterId(userId);
    }

}
