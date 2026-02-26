package com._1zko.hacknovahackathon26backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
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
    private final StringRedisTemplate stringRedisTemplate;
    private final RedisTemplate<Object, Object> redisTemplate;

    @MessageMapping("/arena/submit")
    public void receiveCodeFromPlayer(@Payload Map<String,String> payload){
        String roomId=payload.get("roomId");
        String playerId=payload.get("playerId");

//        Current Status
        String currStatus=payload.get("status");

        System.out.println("⚙️ Received code from " + playerId + " in room " + roomId);

        Map<String,String> statusUpdate=new HashMap<>();
        statusUpdate.put("type","Opponent Status");
        statusUpdate.put("playerId",playerId);
        statusUpdate.put("status",currStatus);

        simpMessagingTemplate.convertAndSend("/room/arena/"+roomId,statusUpdate);

        if("SUBMITTED".equals(currStatus)){
            redisTemplate.delete("room:"+roomId);
            System.out.println("🧹 Match Over! Deleted room " + roomId + " from Redis RAM.");
        }
    }
}
