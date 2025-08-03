package com.example.realtimecommunication.domain.chat.service.sse;

import com.example.realtimecommunication.domain.chat.dto.ChatMessageRequestDto;
import com.example.realtimecommunication.domain.chat.service.sse.manager.SaveMessageManager;
import com.example.realtimecommunication.domain.chat.service.sse.manager.ServerSentEventManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SendMessageService {
    private final ServerSentEventManager serverSentEventManager;
    private final SaveMessageManager saveMessageManager;

    @Transactional
    public void sendMessage(ChatMessageRequestDto dto){
        List<SseEmitter> emitters = serverSentEventManager.getEmitter(dto.roomId());
        saveMessageManager.saveMessage(dto);

        for(SseEmitter emitter : emitters){
            try{
                emitter.send(SseEmitter.event().name("chat").data(dto.content()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
