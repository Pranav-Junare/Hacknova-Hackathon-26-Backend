package com._1zko.hacknovahackathon26backend.service;

import com._1zko.hacknovahackathon26backend.repo.Question;
import com._1zko.hacknovahackathon26backend.repo.QuestionRepository;
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

    // 🔥 1. INJECT THE QUESTION REPOSITORY
    private final QuestionRepository questionRepo;

    private static final Long POINT_TOLERANCE = 100L;

    @Scheduled(fixedDelay = 2000)
    public void processQueues(){
        String[] gameModes = {"dsa", "design"};

        for (String mode : gameModes) {
            String queueKey = "queue:" + mode;

            Set<ZSetOperations.TypedTuple<String>> players = redisTemplate.opsForZSet().rangeWithScores(queueKey, 0, -1);
            if(players == null || players.size() < 2) continue;

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
                        i++;
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

            // 🔥 2. FETCH A RANDOM QUESTION FOR THIS MODE
            Question randomQuestion = questionRepo.findRandomQuestionByMode(mode);
            String qId = randomQuestion != null ? String.valueOf(randomQuestion.getId()) : "1";

            redisTemplate.opsForHash().put(roomKey, "player1", p1);
            redisTemplate.opsForHash().put(roomKey, "player2", p2);
            redisTemplate.opsForHash().put(roomKey, "startTime", String.valueOf(System.currentTimeMillis()));
            redisTemplate.opsForHash().put(roomKey, "questionId", qId); // Save it to Redis too!
            redisTemplate.opsForHash().put(roomKey, "mode", mode);// from claud code
            redisTemplate.expire(roomKey, 1, TimeUnit.HOURS);

            // 🔥 3. PASS THE QUESTION ID TO THE BROADCASTER
            broadcastMatch(p1, p2, roomId, mode, qId);
        }
    }

    // 🔥 4. ADD qId TO THE METHOD SIGNATURE AND PAYLOAD
    private void broadcastMatch(String p1, String p2, String roomId, String mode, String qId) {
        Map<String, String> details = new HashMap<>();
        details.put("roomId", roomId);
        details.put("status", "MATCH_FOUND");
        details.put("mode", mode);
        details.put("questionId", qId); // ⬅️ SENDING TO REACT!

        Map<String,String> p1details = new HashMap<>(details);
        p1details.put("opponent", p2);

        Map<String,String> p2details = new HashMap<>(details);
        p2details.put("opponent", p1);

        messagingTemplate.convertAndSend("/room/match/" + p1, p1details);
        messagingTemplate.convertAndSend("/room/match/" + p2, p2details);
    }
}