package com.example.backend.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/v1/title")
public class SampleController {

    @GetMapping()
    public String getTitle() {
        System.out.println("Requested!");
        return "<title>Hello from back-end!</title>";
    }

}
