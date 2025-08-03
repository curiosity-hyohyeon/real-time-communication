package com.example.realtimecommunication.domain.chat.service.sse;

import com.example.realtimecommunication.domain.chat.service.sse.manager.ServerSentEventManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class SubscribeService {
    private final ServerSentEventManager serverSentEventManager;

    public SseEmitter subscribe(String roomId) {
        try{
            SseEmitter emitter = serverSentEventManager.createSse(roomId);
            emitter.send(SseEmitter.event().name("connect").data("connected"));
            return emitter;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
