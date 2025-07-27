package com.example.realtimecommunication.domain.chat.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ChatMessageResponseDto(
        Long id,
        String sender,
        String content,
        LocalDateTime timestamp
) {
}
