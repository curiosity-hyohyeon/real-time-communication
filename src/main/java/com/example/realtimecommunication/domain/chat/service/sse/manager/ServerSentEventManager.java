package com.example.realtimecommunication.domain.chat.service.sse.manager;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class ServerSentEventManager {
    private final Map<String, List<SseEmitter>> emitterMap = new ConcurrentHashMap<>(); //roomId와 현재 구독중인 클라이언트

    //roomId와 연결된 사용자가의 SSE 연결을 맺고, emitter 반환
    public SseEmitter createSse(String roomId){
        SseEmitter emitter = new SseEmitter(60 * 60 * 1000L);
        emitterMap.computeIfAbsent(roomId, k -> new CopyOnWriteArrayList<>())
                .add(emitter);

        emitter.onCompletion(() -> removeEmitter(roomId, emitter));
        emitter.onTimeout(() -> removeEmitter(roomId, emitter));

        System.out.println("LOG : "+ emitterMap);
        return emitter;
    }


    //해당 방에 존재하는 emitter 가져오기
    public List<SseEmitter> getEmitter(String roomId){
        System.out.println("LOG : "+emitterMap.getOrDefault(roomId, new CopyOnWriteArrayList<>()));
        return emitterMap.getOrDefault(roomId, new CopyOnWriteArrayList<>());
    }

    public void removeEmitter(String roomId, SseEmitter emitter){
        List<SseEmitter> emitters = emitterMap.get(roomId);
        if(emitters != null){ //해당 방이 null이 아니라면
            emitters.remove(emitter); //해당 사용자만 제거
            if (emitters.isEmpty()){ //해당 방이 비어있다면
                emitterMap.remove(roomId); //해당 방 제거
            }
        }
    }
}
