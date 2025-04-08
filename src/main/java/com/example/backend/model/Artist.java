package com.example.backend.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

@Entity
@Table(name = "artists")
@Access(AccessType.FIELD)
public class Artist {
    public Artist() { }
    public Artist(Long id) {
        this.id = id;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    public Long id;

    @Column(name = "name", nullable = false, unique = true)
    public String name;

    @Column(name = "century", nullable = false)
    public String century;

    @ManyToOne
    @JoinColumn(name = "countryid")
    public Country country;

    @JsonIgnore
    @OneToMany
    @JoinColumn(name = "artistid")
    public List<Painting> paintings;
}