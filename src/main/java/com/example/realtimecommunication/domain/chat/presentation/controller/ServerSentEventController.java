package com.example.realtimecommunication.domain.chat.presentation.controller;

import com.example.realtimecommunication.domain.chat.dto.ChatMessageRequestDto;
import com.example.realtimecommunication.domain.chat.service.sse.SendMessageService;
import com.example.realtimecommunication.domain.chat.service.sse.SubscribeService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sse-chat")
public class ServerSentEventController {
    private final SendMessageService sendMessageService;
    private final SubscribeService subscribeService;

    @PostMapping("/send")
    public void publish(@RequestBody @Valid ChatMessageRequestDto dto){
        sendMessageService.sendMessage(dto);
    }

    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@RequestParam @NotBlank(message = "Room ID is required") String roomId){
        return subscribeService.subscribe(roomId);
    }
}
