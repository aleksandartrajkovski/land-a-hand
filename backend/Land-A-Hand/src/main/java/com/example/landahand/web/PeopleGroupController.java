package com.example.landahand.web;

import com.example.landahand.model.PeopleGroup;
import com.example.landahand.service.PeopleGroupService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/pet-types")
@CrossOrigin(origins = {"*"})
public class PeopleGroupController {

    private final PeopleGroupService peopleGroupService;

    public PeopleGroupController(PeopleGroupService peopleGroupService) {
        this.peopleGroupService = peopleGroupService;
    }

    @GetMapping
    public ResponseEntity<List<PeopleGroup>> getPetTypes(){
        return ResponseEntity.ok().body(this.peopleGroupService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<PeopleGroup>> getPetType(@PathVariable Long id){
        return ResponseEntity.ok().body(this.peopleGroupService.findById(id));
    }
}
