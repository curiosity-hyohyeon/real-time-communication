package com.example.realtimecommunication.domain.chat.presentation.controller;

import com.example.realtimecommunication.domain.chat.domain.ChatMessage;
import com.example.realtimecommunication.domain.chat.presentation.dto.ChatMessageDto;
import com.example.realtimecommunication.domain.chat.service.PollingChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class PollingChatController {
    private final PollingChatService polingChatService;

    @GetMapping("/poling-message")
    public List<ChatMessageDto> getMessage(
            @RequestParam String roomId,
            @RequestParam(defaultValue = "0") Long lastMessageId
    ){
        return polingChatService.getNewMessage(roomId, lastMessageId);
    }

    @PostMapping("/send")
    public void sendMessage(@RequestBody ChatMessage message) {
        polingChatService.saveMessage(message);
    }
}
