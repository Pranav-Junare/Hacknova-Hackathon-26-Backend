package com._1zko.hacknovahackathon26backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Home {
    @GetMapping("/")
    public ResponseEntity<?> homePage(){
        return ResponseEntity.ok("Home Page");
    }
}
