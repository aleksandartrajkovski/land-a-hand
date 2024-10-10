package com.example.landahand.service.impl;

import com.example.landahand.model.Task;
import com.example.landahand.model.Review;
import com.example.landahand.model.User;
import com.example.landahand.model.dto.ReviewDto;
import com.example.landahand.model.exceptions.TaskNotFound;
import com.example.landahand.model.exceptions.ReviewNotFound;
import com.example.landahand.model.exceptions.UserNotFound;
import com.example.landahand.model.requests.ReviewRequest;
import com.example.landahand.repository.TaskRepository;
import com.example.landahand.repository.ReviewRepository;
import com.example.landahand.repository.UserRepository;
import com.example.landahand.service.ReviewService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    private final ModelMapper modelMapper;
    public ReviewServiceImpl(ReviewRepository reviewRepository, UserRepository userRepository, TaskRepository taskRepository, ModelMapper modelMapper){
        this.reviewRepository=reviewRepository;
        this.userRepository=userRepository;
        this.taskRepository = taskRepository;
        this.modelMapper=modelMapper;
    }

    @Override
    public List<ReviewDto> findAll() {


        return this.reviewRepository.findAll()
                .stream()
                .map(review -> modelMapper.map(review, ReviewDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ReviewDto> findById(Long id) {
        Review review = reviewRepository.findById(id).orElseThrow(() -> new ReviewNotFound(id));
        ReviewDto reviewDto = modelMapper.map(review, ReviewDto.class);

        return Optional.ofNullable(reviewDto);
    }

    @Override
    public ReviewDto create(ReviewRequest reviewRequest) {
        Review review = new Review();

        User user = userRepository.findById(reviewRequest.userId)
                .orElseThrow(() -> new UserNotFound(reviewRequest.userId));

        Task task = taskRepository.findById(reviewRequest.postId)
                .orElseThrow(() -> new TaskNotFound(reviewRequest.postId));
        review.setUser(user);
        review.setTask(task);
        review.setRating(reviewRequest.rating); //rating pretpostavuvam ke se dade na izbor kako dzvezdicki (ili shepi ha ha) pa nema da pravam checks za vrednosta
        review.setComment(reviewRequest.comment);

        this.reviewRepository.save(review);

        return modelMapper.map(review, ReviewDto.class);
    }

    @Override
    public void delete(Long id) {
        Review review = this.reviewRepository.findById(id)
                .orElseThrow(() -> new ReviewNotFound(id));
        this.reviewRepository.delete(review);
    }
    @Override
    public ReviewDto update(Long id, ReviewRequest reviewRequest) {
        Review review = this.reviewRepository.findById(id).orElseThrow(() -> new ReviewNotFound(id));

        review.setRating(reviewRequest.rating);
        review.setComment(reviewRequest.comment);

        this.reviewRepository.save(review);

        return modelMapper.map(review, ReviewDto.class);
    }
}