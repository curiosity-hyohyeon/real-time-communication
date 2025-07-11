package com.example.realtimecommunication.domain.chat.service;

import com.example.realtimecommunication.domain.chat.domain.ChatMessage;
import com.example.realtimecommunication.domain.chat.domain.repository.ChatMessageRepository;
import com.example.realtimecommunication.domain.chat.presentation.dto.ChatMessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PollingChatService {
    private final ChatMessageRepository chatMessageRepository;

    public List<ChatMessageDto> getNewMessage(String roomId, Long lastMessageId){
        return chatMessageRepository.findByRoomIdAndIdGreaterThanOrderById(roomId, lastMessageId)
                .stream()
                .map(msg -> ChatMessageDto.builder()
                        .id(msg.getId())
                        .sender(msg.getSender())
                        .content(msg.getContent())
                        .timestamp(msg.getTimestamp())
                        .build())
                .collect(Collectors.toList());
    }

    public void saveMessage(ChatMessage message){
        chatMessageRepository.save(
                ChatMessage.builder()
                        .roomId(message.getRoomId())
                        .sender(message.getSender())
                        .content(message.getContent())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }
}
