package com.example.landahand.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "people_group")
public class PeopleGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_id")
    private Long id;

    @Column(name = "type")
    private String type;

    public PeopleGroup() {
    }

    public PeopleGroup(Long id, String type) {
        this.id = id;
        this.type = type;
    }
}
