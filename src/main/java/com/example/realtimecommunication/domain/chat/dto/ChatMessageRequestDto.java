package com.example.realtimecommunication.domain.chat.dto;

import lombok.Builder;

@Builder
public record ChatMessageRequestDto(
        String roomId,
        String sender,
        String content
) {
}
