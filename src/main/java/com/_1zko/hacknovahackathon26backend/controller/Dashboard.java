package com._1zko.hacknovahackathon26backend.controller;

import com._1zko.hacknovahackathon26backend.repo.UserDetails;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class Dashboard {
    @GetMapping("/dashboard")
    public ResponseEntity<?> Dash(HttpSession session){

        Object sesObj=session.getAttribute("currentUser");
        if(sesObj==null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error","Not logged in"));

        UserDetails user=(UserDetails) sesObj;
        return ResponseEntity.ok(Map.of("name", user.getUsername(),"points",user.getPoints()));


    }
}
