package com.example.realtimecommunication.domain.chat.dto;

import com.example.realtimecommunication.domain.chat.domain.ChatMessage;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ChatMessageResponseDto(
        Long id,
        String sender,
        String content,
        LocalDateTime timestamp
) {
    public static ChatMessageResponseDto from(ChatMessage message){
        return ChatMessageResponseDto.builder()
                .id(message.getId())
                .sender(message.getSender())
                .content(message.getContent())
                .timestamp(message.getTimestamp())
                .build();
    }
}
