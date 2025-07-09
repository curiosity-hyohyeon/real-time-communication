package com.example.realtimecommunication.domain.chat.domain.repository;

import com.example.realtimecommunication.domain.chat.domain.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByRoomIdAndIdGreaterThanOrderById(String roomId, Long lastMessageId);
}
