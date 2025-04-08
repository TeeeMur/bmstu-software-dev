package com.example.backend.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.model.Museum;
import com.example.backend.repositories.MuseumRepository;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("api/v1/museums")
public class MuseumController {
    
    @Autowired
    MuseumRepository museumRepository;

    @GetMapping()
    public ResponseEntity<List<Museum>> getAllMuseums() {
        return ResponseEntity.ok(museumRepository.findAll());
    }

    @PostMapping()
    public ResponseEntity<Object> createMuseum(@RequestBody Museum museum) {
        try {
            Museum nm = museumRepository.save(museum);
            return ResponseEntity.ok(nm);
        } catch(Exception ex) {
            String error;
            if (ex.getMessage().contains("museums_name_key")) {
                error = "museumalreadyexists";
            } else {
                error = "undefinederror";
            }
            Map<String, String> map = new HashMap<>();
            map.put("error", error);
            return ResponseEntity.ok(map);
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteMuseum(@PathVariable(value = "id") Long museumId) {
        Optional<Museum> museum = museumRepository.findById(museumId);
        Map<String, Boolean> resp = new HashMap<>();
        if (museum.isPresent()) {
            museumRepository.delete(museum.get());
            resp.put("deleted", Boolean.TRUE);
        } else {
            resp.put("deleted", Boolean.FALSE);
        }
        return ResponseEntity.ok(resp);
    }
}
