package com.example.realtimecommunication.domain.chat.service.long_polling;

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
public class ChatMessageService {
    private final ChatMessageRepository chatMessageRepository;

    @Transactional
    public ChatMessage saveMessage(ChatMessageRequestDto dto){
        ChatMessage entity = ChatMessage.save(dto.roomId(), dto.sender(), dto.content());
        return chatMessageRepository.save(entity);
    }

    public List<ChatMessageResponseDto> getNewMessages(String roomId, Long lastMessageId) {
        return chatMessageRepository.findByRoomIdAndIdGreaterThanOrderById(roomId, lastMessageId).stream()
                .map(ChatMessageResponseDto::from)
                .collect(Collectors.toList());
    }
}
