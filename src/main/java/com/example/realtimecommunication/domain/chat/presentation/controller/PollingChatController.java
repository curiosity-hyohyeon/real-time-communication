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
    private final PollingChatService pollingChatService;

    @GetMapping("/polling-message")
    public List<ChatMessageDto> getMessage(
            @RequestParam @NotBlank(message = "Room ID is required") String roomId,
            @RequestParam(defaultValue = "0") Long lastMessageId
    ){
        return pollingChatService.getNewMessage(roomId, lastMessageId);
    }

    @PostMapping("/send")
    public void sendMessage(@RequestBody ChatMessage message) {
        pollingChatService.saveMessage(message);
    }
}
