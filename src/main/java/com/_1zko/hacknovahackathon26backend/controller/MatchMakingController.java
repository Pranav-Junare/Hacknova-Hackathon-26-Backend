package com._1zko.hacknovahackathon26backend.controller;

import com._1zko.hacknovahackathon26backend.service.MatchMakingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/match")
public class MatchMakingController {

    private final MatchMakingService matchMakingService;

    @PostMapping("/join")
    public ResponseEntity<?> joinQueue(@RequestParam String userName, @RequestParam Long points){
        matchMakingService.joinQueue(userName,points);
        System.out.println("🚪 " + userName + " (Elo: " + points + ") joined the queue!");
        return ResponseEntity.ok(Map.of("message","Successfully joined Queue"));
    }
}
