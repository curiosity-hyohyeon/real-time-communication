package com.example.realtimecommunication.domain.chat.service.sse.manager;

import com.example.realtimecommunication.domain.chat.domain.ChatMessage;
import com.example.realtimecommunication.domain.chat.domain.repository.ChatMessageRepository;
import com.example.realtimecommunication.domain.chat.dto.ChatMessageRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SaveMessageManager {
    private final ChatMessageRepository chatMessageRepository;

    public void saveMessage(ChatMessageRequestDto dto){
        ChatMessage message = ChatMessage.save(dto.roomId(), dto.sender(), dto.content());
        chatMessageRepository.save(message);
    }
}
