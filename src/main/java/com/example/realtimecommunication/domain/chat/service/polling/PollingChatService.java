package com.example.realtimecommunication.domain.chat.service.polling;

import com.example.realtimecommunication.domain.chat.domain.ChatMessage;
import com.example.realtimecommunication.domain.chat.domain.repository.ChatMessageRepository;
import com.example.realtimecommunication.domain.chat.dto.ChatMessageRequestDto;
import com.example.realtimecommunication.domain.chat.dto.ChatMessageResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PollingChatService {
    private final ChatMessageRepository chatMessageRepository;

    public List<ChatMessageResponseDto> getNewMessage(String roomId, Long lastMessageId){
        return chatMessageRepository.findByRoomIdAndIdGreaterThanOrderById(roomId, lastMessageId)
                .stream()
                .map(msg -> ChatMessageResponseDto.builder()
                        .id(msg.getId())
                        .sender(msg.getSender())
                        .content(msg.getContent())
                        .timestamp(msg.getTimestamp())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public void saveMessage(ChatMessageRequestDto message){
        ChatMessage chatMessage = ChatMessage.save(message.roomId(), message.sender(), message.content());
        chatMessageRepository.save(chatMessage);
    }
}
