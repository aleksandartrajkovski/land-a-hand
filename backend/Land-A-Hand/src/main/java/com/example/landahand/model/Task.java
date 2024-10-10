package com.example.landahand.model;

import com.example.landahand.model.enums.ImportanceLevel;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "task")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_id")
    private Long id;

    @Column(name = "description")
    private String description;

    @Column(name = "city")
    private String city;

    @Column(name = "task_importance_level")
    @Enumerated(EnumType.STRING)
    private ImportanceLevel importanceLevel;

    @Column(name = "points")
    private double price;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "task_type_id")
    private TaskType taskType;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "title")
    private String title;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<TaskPicture> pictures = new ArrayList<>();

    public Task() {
    }

    public Task(Long id, String description, ImportanceLevel importanceLevel, double price, TaskType taskType, User user, String city, String title) {
        this.id = id;
        this.description = description;
        this.importanceLevel = importanceLevel;
        this.price = price;
        this.taskType = taskType;
        this.user = user;
        this.title = title;
        this.city=city;
    }


    public List<TaskPicture> getPictures() {
        return pictures;
    }
    public void setPictures(List<TaskPicture> pictures)
    {
        this.pictures=pictures;
    }
}
