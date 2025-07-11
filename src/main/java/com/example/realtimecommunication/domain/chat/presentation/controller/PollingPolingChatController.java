package com.example.realtimecommunication.domain.chat.presentation.controller;

import com.example.realtimecommunication.domain.chat.domain.ChatMessage;
import com.example.realtimecommunication.domain.chat.presentation.dto.ChatMessageDto;
import com.example.realtimecommunication.domain.chat.service.LongPollingChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.List;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class PollingPolingChatController {
    private final LongPollingChatService longPolingChatService;

    @GetMapping("/long-poling-subscribe")
    public DeferredResult<List<ChatMessageDto>> subscribe(
            @RequestParam String roomId,
            @RequestParam(defaultValue = "0") Long lastMessageId
    ){
        return longPolingChatService.waitForNewMessages(roomId, lastMessageId);
    }

    @PostMapping("/long-poling-send")
    public ResponseEntity<Void> sendMessage(@RequestBody ChatMessage message){
        longPolingChatService.saveMessage(message);
        return ResponseEntity.ok().build();
    }

}
