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
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.model.Artist;
import com.example.backend.model.Painting;
import com.example.backend.model.Museum;
import com.example.backend.repositories.ArtistRepository;
import com.example.backend.repositories.MuseumRepository;
import com.example.backend.repositories.PaintingRepository;
import com.example.backend.tools.DataValidationException;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("api/v1/paintings")
public class PaintingController {

    @Autowired
    PaintingRepository paintingRepository;

    @Autowired
    ArtistRepository artistRepository;

    @Autowired
    MuseumRepository museumRepository;

    @GetMapping("/{id}")
    public ResponseEntity<Object> getPainting(@PathVariable(value = "id") Long paintingId)
            throws DataValidationException {
        Painting painting = paintingRepository.findById(paintingId)
                .orElseThrow(() -> new DataValidationException("Картина с таким индексом не найдена"));
        return ResponseEntity.ok(painting);
    }

    @GetMapping()
    public ResponseEntity<Page<Painting>> getAllPaintings(@RequestParam("page") int page, @RequestParam("limit") int limit) {
        return ResponseEntity.ok(paintingRepository.findAll(PageRequest.of(page, limit, Sort.by(Sort.Direction.ASC, "name"))));
    }

    @PostMapping()
    public ResponseEntity<Object> createPainting(@RequestBody List<Painting> paintings) {
        Map<String, String> map = new HashMap<>();
        for (Painting p : paintings) {
            try {
                if (p.museum.name != "") {
                    Optional<Museum> m = museumRepository.findByName(p.museum.name);
                    if (m.isPresent()) {
                        p.museum = m.get();
                    }
                }
                if (p.artist.name != "") {
                    Optional<Artist> a = artistRepository.findByName(p.artist.name);
                    if (a.isPresent()) {
                        p.artist = a.get();
                    }
                }
                Painting nm = paintingRepository.save(p);
                map.put(nm.name, "success");
            } catch (Exception ex) {
                String error = "Неизвестная ошибка";
                if (ex.getMessage().contains("Painting")) {
                    error = "Такая картина уже есть!";
                } else if (ex.getMessage().contains("Artist")) {
                    error = "Автор не найден";
                } else if (ex.getMessage().contains("Museum")) {
                    error = "Музей не найден";
                }
                System.out.println(ex.getMessage());
                map.put("data", error);
                return ResponseEntity.status(HttpStatusCode.valueOf(400)).body(map);
            }
        }
        return ResponseEntity.ok(map);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Painting> updatePainting(@PathVariable(value = "id") Long paintingId,
            @RequestBody Painting paintingDetails) throws DataValidationException {
        try {
            Painting painting = paintingRepository.findById(paintingId)
                    .orElseThrow(() -> new DataValidationException("Страна с таким индексом не найдена"));
            painting.name = paintingDetails.name;
            paintingRepository.save(painting);
            return ResponseEntity.ok(painting);
        } catch (Exception ex) {
            if (ex.getMessage().contains("paintings_name_key"))
                throw new DataValidationException("Эта страна уже есть в базе");
            else
                throw new DataValidationException("Неизвестная ошибка");
        }
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

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @PostMapping("/deletepaintings")
    public ResponseEntity<Object> deletePaintings(@RequestBody List<Painting> paintings) {
        paintingRepository.deleteAll(paintings);
        return new ResponseEntity(HttpStatus.OK);
    }
}
