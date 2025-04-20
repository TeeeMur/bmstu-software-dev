package com.example.backend.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.model.User;

public interface UserRepository extends JpaRepository<User, Long>{

    Optional<User> findByToken(String token);

    Optional<User> findByLogin(String login);

}
