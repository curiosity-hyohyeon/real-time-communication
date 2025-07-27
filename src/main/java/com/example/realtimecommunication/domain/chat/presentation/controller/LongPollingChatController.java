package com.example.realtimecommunication.domain.chat.presentation.controller;

import com.example.realtimecommunication.domain.chat.dto.ChatMessageRequestDto;
import com.example.realtimecommunication.domain.chat.dto.ChatMessageResponseDto;
import com.example.realtimecommunication.domain.chat.service.long_polling.LongPollingChatService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.List;

@Validated
@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class LongPollingChatController {
    private final LongPollingChatService longPollingChatService;

    @GetMapping("/long-polling-subscribe")
    public DeferredResult<List<ChatMessageResponseDto>> subscribe(
            @RequestParam @NotBlank(message = "Room ID is required") String roomId,
            @RequestParam(defaultValue = "0") Long lastMessageId
    ){
        return longPollingChatService.waitForNewMessages(roomId, lastMessageId);
    }

    @PostMapping("/long-polling-send")
    public void sendMessage(@RequestBody ChatMessageRequestDto message){
        longPollingChatService.saveMessage(message);
    }

}
