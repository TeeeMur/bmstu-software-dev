package com.example.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.model.Painting;

public interface PaintingRepository extends JpaRepository<Painting, Long>{
    
}
