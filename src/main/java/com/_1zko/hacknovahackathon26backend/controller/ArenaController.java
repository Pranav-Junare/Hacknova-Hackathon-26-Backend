package com._1zko.hacknovahackathon26backend.controller;

import com._1zko.hacknovahackathon26backend.repo.UserDB;
import com._1zko.hacknovahackathon26backend.repo.UserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ArenaController {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final UserDB userDB;
    private final RedisTemplate<String, String> redisTemplate;

    @MessageMapping("/arena/submit")
    public void receiveCodeFromPlayer(@Payload Map<String,String> payload){
        String roomId = payload.get("roomId");
        String playerId = payload.get("playerId");
        String currStatus = payload.get("status");
        String code = payload.get("code");

        // --- NEW: GRAB THE MODE FROM REACT! ---
        String mode = payload.getOrDefault("mode", "dsa");

        System.out.println("⚙️ Received status [" + currStatus + "] from " + playerId + " in room " + roomId + " (Mode: " + mode + ")");

        // 1. Broadcast the current status to the opponent
        Map<String,String> statusUpdate = new HashMap<>();
        statusUpdate.put("type", "Opponent Status");
        statusUpdate.put("playerId", playerId);
        statusUpdate.put("status", currStatus);

        simpMessagingTemplate.convertAndSend("/room/arena/" + roomId, statusUpdate);

        // 2. THE DUAL JUDGE LOGIC
        if("SUBMITTED".equals(currStatus)){
            System.out.println("🧪 Evaluating " + mode + " submission for " + playerId + "...");

            // Pass the mode into the evaluation function!
            boolean passedAllTests = evaluateSubmission(code, mode);

            Map<String,String> finalResult = new HashMap<>();
            finalResult.put("type", "Game Over");
            finalResult.put("playerId", playerId);

            if (passedAllTests) {
                finalResult.put("status", "VICTORY");
                System.out.println("🏆 " + playerId + " WON THE " + mode.toUpperCase() + " MATCH!");

                updateDynamicElo(playerId, roomId);

                simpMessagingTemplate.convertAndSend("/room/arena/" + roomId, finalResult);
                redisTemplate.delete("room:" + roomId);
                System.out.println("🧹 Match Over! Deleted room " + roomId + " from Redis RAM.");
            } else {
                finalResult.put("status", "FAILED");
                System.out.println("❌ " + playerId + "'s " + mode + " submission failed.");

                simpMessagingTemplate.convertAndSend("/room/arena/" + roomId, finalResult);
            }
        }
    }

    // --- THE DUAL HACKATHON MOCK COMPILER ---
    private boolean evaluateSubmission(String code, String mode) {
        if (code == null) return false;

        if ("dsa".equals(mode)) {
            // DSA Mode: Grade the C++ logic
            String strippedCode = code.replaceAll("\\s+", "");
            return strippedCode.contains("return{0,1}") ||
                    strippedCode.contains("return[0,1]") ||
                    strippedCode.contains("returnvector<int>{0,1}");
        }
        else if ("design".equals(mode)) {
            // System Design Mode: Grade the React Flow JSON
            // Check if they deployed a Load Balancer, Server, Database, and wired them up
            return code.contains("Load Balancer") &&
                    code.contains("Server") &&
                    code.contains("Database") &&
                    code.contains("source"); // "source" proves they drew a wire between nodes
        }

        return false;
    }

    // --- DYNAMIC SPEED ELO REMAINS UNCHANGED ---
    private void updateDynamicElo(String winnerUsername, String roomId) {
        String roomKey = "room:" + roomId;
        String p1 = (String) redisTemplate.opsForHash().get(roomKey, "player1");
        String p2 = (String) redisTemplate.opsForHash().get(roomKey, "player2");
        String startTimeStr = (String) redisTemplate.opsForHash().get(roomKey, "startTime");

        if (p1 == null || p2 == null || startTimeStr == null) return;

        String loserUsername = winnerUsername.equals(p1) ? p2 : p1;

        UserDetails winner = userDB.findByUsername(winnerUsername);
        UserDetails loser = userDB.findByUsername(loserUsername);

        if (winner == null || loser == null) return;

        long startTime = Long.parseLong(startTimeStr);
        long timeTakenSeconds = (System.currentTimeMillis() - startTime) / 1000;

        double speedBonus = Math.max(0, (1800.0 - timeTakenSeconds) / 1800.0) * 30.0;
        double kFactor = 20.0 + speedBonus;

        double expectedWinner = 1.0 / (1.0 + Math.pow(10, (loser.getPoints() - winner.getPoints()) / 400.0));
        double expectedLoser = 1.0 / (1.0 + Math.pow(10, (winner.getPoints() - loser.getPoints()) / 400.0));

        long winnerGain = Math.round(kFactor * (1.0 - expectedWinner));
        long loserLoss = Math.round(kFactor * (0.0 - expectedLoser));

        winner.setPoints(winner.getPoints() + winnerGain);
        loser.setPoints(Math.max(0, loser.getPoints() + loserLoss));

        userDB.save(winner);
        userDB.save(loser);

        System.out.println("📈 ELO UPDATE | " + winnerUsername + " +" + winnerGain + " | " + loserUsername + " " + loserLoss + " (Time: " + timeTakenSeconds + "s)");
    }
}