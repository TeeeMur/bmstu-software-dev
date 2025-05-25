package com.example.backend.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.model.Museum;
import com.example.backend.repositories.MuseumRepository;
import com.example.backend.tools.DataValidationException;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("api/v1/museums")
public class MuseumController {
    
    @Autowired
    MuseumRepository museumRepository;

    @GetMapping()
    public Page<Museum> getAllMuseums(@RequestParam("page") int page, @RequestParam("limit") int limit) {
        return museumRepository.findAll(PageRequest.of(page, limit, Sort.by(Sort.Direction.ASC, "name")));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Museum> getMuseum(@PathVariable(value = "id") Long museumId)
            throws DataValidationException
    {
        Museum museum = museumRepository.findById(museumId)
                .orElseThrow(()-> new DataValidationException("Музей с таким индексом не найден"));
        return ResponseEntity.ok(museum);
    }
    

    @PutMapping("/{id}")
    public ResponseEntity<Museum> updateMuseum(@PathVariable(value = "id") Long museumId,
            @RequestBody Museum museumDetails) throws DataValidationException {
        try {
            Museum museum = museumRepository.findById(museumId)
                    .orElseThrow(() -> new DataValidationException("Музей с таким индексом не найден"));
            museum.name = museumDetails.name;
            if (museumDetails.location != null) {
                museum.location = museumDetails.location;
            }
            museumRepository.save(museum);
            return ResponseEntity.ok(museum);
        } catch (Exception ex) {
            if (ex.getMessage().contains("museums_name_key"))
                throw new DataValidationException("Этот музей уже есть в базе");
            else
                throw new DataValidationException("Неизвестная ошибка");
        }
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

    @SuppressWarnings("rawtypes")
    @PostMapping("/deletemuseums")
    public ResponseEntity deleteMuseums(@Validated @RequestBody List<Museum> museums) {
        museumRepository.deleteAll(museums);
        return new ResponseEntity(HttpStatus.OK);
    }

}
