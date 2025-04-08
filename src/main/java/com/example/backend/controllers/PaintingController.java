package com.example.backend.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.model.Artist;
import com.example.backend.model.Museum;
import com.example.backend.model.Painting;
import com.example.backend.repositories.ArtistRepository;
import com.example.backend.repositories.MuseumRepository;
import com.example.backend.repositories.PaintingRepository;

@RestController
@RequestMapping("api/v1/paintings")
public class PaintingController {

    @Autowired
    PaintingRepository paintingRepository;

    @Autowired
    ArtistRepository artistRepository;

    @Autowired
    MuseumRepository museumRepository;

    @GetMapping()
    public ResponseEntity<List<Painting>> getAllPaintings() {
        return ResponseEntity.ok(paintingRepository.findAll());
    }

    @PostMapping()
    public ResponseEntity<Object> createPainting(@RequestBody List<Painting> paintings) {
        Map<String, String> map = new HashMap<>();
        for (Painting p : paintings) {
            try {
                if (p.museum != null) {
                    Optional<Museum> m = museumRepository.findById(p.museum.id);
                    if (m.isPresent()) {
                        p.museum = m.get();
                    }
                }
                if (p.artist != null) {
                    Optional<Artist> a = artistRepository.findById(p.artist.id);
                    if (a.isPresent()) {
                        p.artist = a.get();
                    }
                }
                Painting nm = paintingRepository.save(p);
                map.put(nm.name, "success");
            } catch (Exception ex) {
                String error;
                if (ex.getMessage().contains("paintings_name_key")) {
                    error = "paintingalreadyexists";
                } else {
                    error = "undefinederror";
                }
                map.put(p.name, error);
            }
        }
        return ResponseEntity.ok(map);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deletePainting(@PathVariable(value = "id") Long paintingId) {
        Optional<Painting> painting = paintingRepository.findById(paintingId);
        Map<String, Boolean> resp = new HashMap<>();
        if (painting.isPresent()) {
            paintingRepository.delete(painting.get());
            resp.put("deleted", Boolean.TRUE);
        } else {
            resp.put("deleted", Boolean.FALSE);
        }
        return ResponseEntity.ok(resp);
    }
}
