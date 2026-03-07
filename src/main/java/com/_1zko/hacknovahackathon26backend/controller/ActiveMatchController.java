// NEW FILE: controller/ActiveMatchController.java
package com._1zko.hacknovahackathon26backend.controller;

import com._1zko.hacknovahackathon26backend.repo.UserDetails;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/match")
@RequiredArgsConstructor
public class ActiveMatchController {

    private final RedisTemplate<String, String> redisTemplate;

    @GetMapping("/active")
    public ResponseEntity<?> getActiveMatch(HttpSession session) {
        Object sesObj = session.getAttribute("currentUser");
        if (sesObj == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Not logged in"));
        }

        String username = ((UserDetails) sesObj).getUsername();

        // Scan all room:* keys to find one containing this player
        Set<String> roomKeys = redisTemplate.keys("room:*");
        if (roomKeys == null) {
            return ResponseEntity.ok(Map.of("active", false));
        }

        for (String roomKey : roomKeys) {
            String p1         = (String) redisTemplate.opsForHash().get(roomKey, "player1");
            String p2         = (String) redisTemplate.opsForHash().get(roomKey, "player2");
            String questionId = (String) redisTemplate.opsForHash().get(roomKey, "questionId");
            String mode       = (String) redisTemplate.opsForHash().get(roomKey, "mode");

            if (username.equals(p1) || username.equals(p2)) {
                String roomId   = roomKey.replace("room:", "");
                String opponent = username.equals(p1) ? p2 : p1;
                return ResponseEntity.ok(Map.of(
                        "active",     true,
                        "roomId",     roomId,
                        "opponent",   opponent != null ? opponent : "",
                        "questionId", questionId != null ? questionId : "1",
                        "mode",       mode != null ? mode : "dsa"
                ));
            }
        }

        return ResponseEntity.ok(Map.of("active", false));
    }
}