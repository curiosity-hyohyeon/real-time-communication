package com.example.realtimecommunication.domain.chat.presentation.controller;

import com.example.realtimecommunication.domain.chat.dto.ChatMessageRequestDto;
import com.example.realtimecommunication.domain.chat.dto.ChatMessageResponseDto;
import com.example.realtimecommunication.domain.chat.service.polling.PollingChatService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class PollingChatController {
    private final PollingChatService pollingChatService;

    @GetMapping("/polling-message")
    public List<ChatMessageResponseDto> getMessage(
            @RequestParam @NotBlank(message = "Room ID is required") String roomId,
            @RequestParam(defaultValue = "0") Long lastMessageId
    ){
        return pollingChatService.getNewMessage(roomId, lastMessageId);
    }

    @PostMapping("/send")
    public void sendMessage(@RequestBody ChatMessageRequestDto message) {
        pollingChatService.saveMessage(message);
    }
}
