package com.example.landahand.model;

import jakarta.persistence.*;

@Entity
@Table(name = "task_pictures")
public class TaskPicture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Lob
    @Column(name = "pictures", nullable = false)
    private byte[] picture;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    private Task task;

    public TaskPicture(){

    }
    public TaskPicture(byte[] picture, Task task)
    {
        this.picture=picture;
        this.task=task;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id)
    {
        this.id=id;
    }
    public byte[] getPicture() {
        return picture;
    }
    public void setPicture(byte[] picture){
        this.picture=picture;
    }
    public Task getTask(){
        return task;
    }
    public void setTask(Task task)
    {
        this.task=task;
    }

}
