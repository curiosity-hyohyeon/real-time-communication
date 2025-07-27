package com.example.realtimecommunication.domain.chat.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@Table(name = "chat_message")
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String roomId;

    @Column(nullable = false, length = 20)
    private String sender;

    @Column(nullable = false, length = 1000)
    private String content;

    @Column(nullable = false)
    private LocalDateTime timestamp;


    @PrePersist
    public void setTimestamp(){
        this.timestamp = LocalDateTime.now();
    }

    public static ChatMessage save(String roomId, String sender, String content){
        return ChatMessage.builder()
                .roomId(roomId)
                .sender(sender)
                .content(content)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
