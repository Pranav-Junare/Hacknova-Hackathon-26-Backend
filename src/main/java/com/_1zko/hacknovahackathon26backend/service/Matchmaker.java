package com._1zko.hacknovahackathon26backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class Matchmaker {

    private final StringRedisTemplate redisTemplate;
    private final SimpMessagingTemplate messagingTemplate;

    private static final Long POINT_TOLERANCE = 100L;

    @Scheduled(fixedDelay = 2000)
    public void processQueues(){
        // 1. WE NOW CHECK BOTH QUEUES!
        String[] gameModes = {"dsa", "design"};

        for (String mode : gameModes) {
            String queueKey = "queue:" + mode;

            Set<ZSetOperations.TypedTuple<String>> players = redisTemplate.opsForZSet().rangeWithScores(queueKey, 0, -1);
            if(players == null || players.size() < 2) continue; // Skip to next mode if not enough players

            System.out.println("🔍 Matchmaker checking " + players.size() + " players in " + queueKey + "...");
            List<ZSetOperations.TypedTuple<String>> playerList = new ArrayList<>(players);

            for(int i = 0; i < playerList.size() - 1; i++){
                ZSetOperations.TypedTuple<String> p1 = playerList.get(i);
                ZSetOperations.TypedTuple<String> p2 = playerList.get(i+1);

                if (p1.getScore() != null && p2.getScore() != null){
                    double diff = Math.abs(p1.getScore() - p2.getScore());

                    if(diff <= POINT_TOLERANCE){
                        System.out.println("✅ " + mode.toUpperCase() + " Match Found! Creating room...");

                        createMatchIfSecured(p1.getValue(), p2.getValue(), mode, queueKey);
                        i++; // Skip the next player as they are now in a match
                    }
                }
            }
        }
    }

    private void createMatchIfSecured(String p1, String p2, String mode, String queueKey){
        Long removedCount = redisTemplate.opsForZSet().remove(queueKey, p1, p2);

        if(removedCount != null && removedCount == 2){
            String roomId = UUID.randomUUID().toString();
            String roomKey = "room:" + roomId;

            // 2. SAVING START TIME FOR DYNAMIC SPEED ELO
            redisTemplate.opsForHash().put(roomKey, "player1", p1);
            redisTemplate.opsForHash().put(roomKey, "player2", p2);
            redisTemplate.opsForHash().put(roomKey, "startTime", String.valueOf(System.currentTimeMillis()));

            // Destroy the room after 1 hr
            redisTemplate.expire(roomKey, 1, TimeUnit.HOURS);

            broadcastMatch(p1, p2, roomId, mode);
        }
    }

    private void broadcastMatch(String p1, String p2, String roomId, String mode) {
        Map<String, String> details = new HashMap<>();
        details.put("roomId", roomId);
        details.put("status", "MATCH_FOUND");

        // 3. TELL REACT WHICH ARENA TO OPEN
        details.put("mode", mode);

        Map<String,String> p1details = new HashMap<>(details);
        p1details.put("opponent", p2);

        Map<String,String> p2details = new HashMap<>(details);
        p2details.put("opponent", p1);

        messagingTemplate.convertAndSend("/room/match/" + p1, p1details);
        messagingTemplate.convertAndSend("/room/match/" + p2, p2details);
    }
}