package com.example.backend.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.backend.model.Artist;
import com.example.backend.model.Country;
import com.example.backend.repositories.ArtistRepository;
import com.example.backend.repositories.CountryRepository;
import com.example.backend.tools.DataValidationException;

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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("api/v1/artists")
class ArtistController{

    @Autowired
    private ArtistRepository artistRepository;
    @Autowired
    private CountryRepository countryRepository;

    @GetMapping("")
    public ResponseEntity<Page<Artist>> getAllArtists(@RequestParam("page") int page, @RequestParam("limit") int limit) {
        return ResponseEntity.ok(artistRepository.findAll(PageRequest.of(page, limit, Sort.by(Sort.Direction.ASC, "name"))));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Artist> getArtist(@PathVariable(value = "id") Long artistId)
            throws DataValidationException
    {
        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(()-> new DataValidationException("Художник с таким индексом не найден"));
        return ResponseEntity.ok(artist);
    }
    
    @PostMapping()
    public ResponseEntity<Object> createArtist(@RequestBody Artist artist) {
        try {
            Optional<Country> cc = countryRepository.findById(artist.country.id);
            if (cc.isPresent()) {
                artist.country = cc.get();
            }
            Artist na = artistRepository.save(artist);
            return ResponseEntity.ok(na);
        } catch (Exception ex) {
            String error = "undefinederror";
            if (ex.getMessage().contains("artists_name_key")) {
                error = "artistalreadyexists";
            }
            Map<String, String> map = new HashMap<>();
            map.put("error", error);
            return ResponseEntity.ok(map);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Artist> updateArtist(@PathVariable(value = "id") Long artistId, 
        @RequestBody Artist artistDetails) {
        Artist artist = null;
        Optional<Artist> artistToUpdate = artistRepository.findById(artistId);
        if (artistToUpdate.isPresent()) {
            artist = artistToUpdate.get();
            artist.name = artistDetails.name;
            artistRepository.save(artist);
            return ResponseEntity.ok(artist);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "artist not found");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteArtist(@PathVariable(value = "id") Long artistId) {
        Optional<Artist> artist = artistRepository.findById(artistId);
        Map<String, Boolean> resp = new HashMap<>();
        if (artist.isPresent()) {
            artistRepository.delete(artist.get());
            resp.put("deleted", Boolean.TRUE);
        } else {
            resp.put("deleted", Boolean.FALSE);
        }
        return ResponseEntity.ok(resp);
    }

    @SuppressWarnings("rawtypes")
    @PostMapping("/deleteartists")
    public ResponseEntity deleteArtists(@Validated @RequestBody List<Artist> artists) {
        artistRepository.deleteAll(artists);
        return new ResponseEntity(HttpStatus.OK);
    }
}