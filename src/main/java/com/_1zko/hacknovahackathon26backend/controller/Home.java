package com._1zko.hacknovahackathon26backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Home {
    @GetMapping("/")
    public String homePage(){
        return "This is the home page";
    }
}
