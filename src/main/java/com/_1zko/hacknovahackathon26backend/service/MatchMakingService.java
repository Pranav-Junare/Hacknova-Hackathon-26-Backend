package com._1zko.hacknovahackathon26backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MatchMakingService {

    private final StringRedisTemplate redisTemplate;
    private String mode;

    // Updated to handle mode-based queues (queue:dsa or queue:design)
    public void joinQueue(String userName, Long points) {
        String queueKey = "queue:" + (mode != null ? mode : "dsa");
        redisTemplate.opsForZSet().add(queueKey, userName, points);
    }

    // Updated to remove user from all possible queues to prevent "ghost" entries
    public void leaveQueue(String userName) {
        redisTemplate.opsForZSet().remove("queue:dsa", userName);
        redisTemplate.opsForZSet().remove("queue:design", userName);
    }
}