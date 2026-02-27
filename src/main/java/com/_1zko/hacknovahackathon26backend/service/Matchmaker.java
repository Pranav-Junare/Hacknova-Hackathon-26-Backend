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

    private static final String QUEUE_KEY="matchmaking_queue";
    private static final Long POINT_TOLERANCE=100L;
    @Scheduled(fixedDelay = 2000)
    public void processQueue(){
        Set<ZSetOperations.TypedTuple<String>> players=redisTemplate.opsForZSet().rangeWithScores(QUEUE_KEY,0,100);
        if(players==null||players.size()<2) return;

        System.out.println("🔍 Matchmaker checking " + players.size() + " players...");
        List<ZSetOperations.TypedTuple<String>> playerList=new ArrayList<>(players);

        for(int i=0;i<playerList.size()-1;i++){
            ZSetOperations.TypedTuple<String> p1=playerList.get(i);
            ZSetOperations.TypedTuple<String> p2=playerList.get(i+1);

            if (p1.getScore()!=null && p2.getScore()!=null){
                double diff = Math.abs(p1.getScore() - p2.getScore());
                System.out.println("Comparing: " + p1.getValue() + " vs " + p2.getValue() + " | Diff: " + diff);
                if(diff<=POINT_TOLERANCE){

                    System.out.println("✅ Match Found! Creating room...");

                    String player1=p1.getValue();
                    String player2=p2.getValue();
                    createMatchIfSecured(player1,player2);

                    i++;
                }
            }
        }
    }
    private void createMatchIfSecured(String p1,String p2){
        Long removedCount=redisTemplate.opsForZSet().remove(QUEUE_KEY,p1,p2);

        if(removedCount!=null && removedCount==2){
            String roomId= UUID.randomUUID().toString();
            String roomKey="room:"+roomId;
            redisTemplate.opsForHash().put("room:" + roomId,p1, "connected");
            redisTemplate.opsForHash().put("room:" + roomId,p2, "connected");

//            Destory the room after 1 hr
            redisTemplate.expire(roomKey, 1, TimeUnit.HOURS);

//            Websockets req from here
            broadcastMatch(p1,p2,roomId);
        }
    }

    private void broadcastMatch(String p1, String p2, String roomId) {

        Map<String, String> details=new HashMap<>();
        details.put("roomId", roomId);
        details.put("status","MATCH_FOUND");

        Map<String,String>p1details = new HashMap<>(details);
        p1details.put("opponent",p2);
        Map<String,String>p2details = new HashMap<>(details);
        p2details.put("opponent",p1);

        messagingTemplate.convertAndSend("/room/match/"+p1,p1details);
        messagingTemplate.convertAndSend("/room/match/"+p2,p2details);
    }
}
