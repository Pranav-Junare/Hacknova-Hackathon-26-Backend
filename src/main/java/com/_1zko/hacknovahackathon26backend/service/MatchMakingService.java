package com._1zko.hacknovahackathon26backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.support.SimpleTriggerContext;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MatchMakingService {

    private final StringRedisTemplate redisTemplate;
    private static final String QUEUE_KEY="matchmaking_queue";

    // Call this when the user clicks "Find Match"
    public void joinQueue(String userName, Long points){
        redisTemplate.opsForZSet().add(QUEUE_KEY,userName, points);
    }
    public void leaveQueue(String userName){
        redisTemplate.opsForZSet().remove(QUEUE_KEY, userName);
    }
}
