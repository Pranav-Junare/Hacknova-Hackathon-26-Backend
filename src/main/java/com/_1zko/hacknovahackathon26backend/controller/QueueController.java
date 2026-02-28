package com._1zko.hacknovahackathon26backend.controller;

import com._1zko.hacknovahackathon26backend.repo.UserDB;
import com._1zko.hacknovahackathon26backend.repo.UserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/queue")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true") // Allows React to talk to it
@RequiredArgsConstructor
public class QueueController {

    private final StringRedisTemplate redisTemplate;
    private final UserDB userDB;

    @PostMapping("/join")
    public ResponseEntity<?> joinQueue(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String mode = request.getOrDefault("mode", "dsa"); // "dsa" or "design"

        if (username == null || username.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Username is missing!"));
        }

        // 1. Get their Elo score from MySQL
        UserDetails user = userDB.findByUsername(username);

        // Default hackathon fallback just in case the DB search fails
        long eloPoints = 1200L;
        if (user != null && user.getPoints() != null) {
            eloPoints = user.getPoints();
        }

        // 2. Add them to the correct Redis Sorted Set
        String queueKey = "queue:" + mode;
        redisTemplate.opsForZSet().add(queueKey, username, eloPoints);

        System.out.println("🚪 SUCCESS: " + username + " joined " + queueKey + " with " + eloPoints + " Elo!");

        return ResponseEntity.ok(Map.of("status", "Queued", "queue", queueKey));
    }
    @GetMapping("/api/users/{username}/elo")
    public ResponseEntity<?> getUserElo(@PathVariable String username) {
        // 1. Find the user in the database
        UserDetails user = userDB.findByUsername(username);

        if (user == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "User not found"));
        }

        // 2. Return their fresh, up-to-date points!
        return ResponseEntity.ok(Map.of("elo", user.getPoints()));
    }
}