package com.example.realtimecommunication.domain.chat.presentation.controller;

import com.example.realtimecommunication.domain.chat.domain.ChatMessage;
import com.example.realtimecommunication.domain.chat.presentation.dto.ChatMessageDto;
import com.example.realtimecommunication.domain.chat.service.LongPollingChatService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public DeferredResult<List<ChatMessageDto>> subscribe(
            @RequestParam @NotBlank(message = "Room ID is required") String roomId,
            @RequestParam(defaultValue = "0") Long lastMessageId
    ){
        return longPollingChatService.waitForNewMessages(roomId, lastMessageId);
    }

    @PostMapping("/long-polling-send")
    public ResponseEntity<Void> sendMessage(@RequestBody ChatMessage message){
        longPollingChatService.saveMessage(message);
        return ResponseEntity.ok().build();
    }

}
