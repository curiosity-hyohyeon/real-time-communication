package com.example.realtimecommunication.domain.chat.presentation.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ChatMessageDto(
        Long id,
        String sender,
        String content,
        LocalDateTime timestamp
) {
}
