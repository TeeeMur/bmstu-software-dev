package com.example.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "paintings")
@Access(AccessType.FIELD)
public class Painting {
    
    Painting() {}

    Painting(Long id) {
        this.id = id;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    public Long id;
    
    @Column(name = "name", nullable = false)
    public String name;

    @ManyToOne
    @JoinColumn(name = "artistid")
    public Artist artist;

    @ManyToOne
    @JoinColumn(name = "museumid")
    public Museum museum;

    @Column(name = "year", updatable = false, nullable = false)
    public Integer year;
}
