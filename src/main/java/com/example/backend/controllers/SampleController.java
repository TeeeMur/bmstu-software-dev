package com.example.backend.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/api/v1/title")
public class SampleController {

    @GetMapping()
    public String getTitle() {
        System.out.println("Requested!");
        return "<title>Hello from back-end!</title>";
    }
    

}
