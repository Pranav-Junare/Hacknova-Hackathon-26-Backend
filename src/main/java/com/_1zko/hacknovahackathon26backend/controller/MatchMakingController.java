package com._1zko.hacknovahackathon26backend.controller;

import com._1zko.hacknovahackathon26backend.repo.UserDetails;
import com._1zko.hacknovahackathon26backend.service.MatchMakingService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<?> joinQueue(HttpSession session){
        Object sessionObj=session.getAttribute("currentUser");

        if(sessionObj==null)return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Not logged in"));
        UserDetails currUser=(UserDetails) sessionObj;

        matchMakingService.joinQueue(currUser.getUsername(),currUser.getPoints());
        System.out.println("🚪 " + currUser.getUsername() + " (Elo: " + currUser.getPoints() + ") joined the queue!");
        return ResponseEntity.ok(Map.of("message","Successfully joined Queue"));
    }
    @PostMapping("/leave")
    public ResponseEntity<?> leaveQueue(HttpSession session){

        Object sessionObj=session.getAttribute("currentUser");

        if(sessionObj==null)return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Not logged in"));
        UserDetails currUser=(UserDetails) sessionObj;

        matchMakingService.leaveQueue(currUser.getUsername());

        System.out.println("🚪 " + currUser.getUsername() + " left the queue.");
        return ResponseEntity.ok(Map.of("message","Successfully left Queue"));
    }
}
