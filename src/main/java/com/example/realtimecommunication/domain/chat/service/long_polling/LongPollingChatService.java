package com.example.realtimecommunication.domain.chat.service.long_polling;

import com.example.realtimecommunication.domain.chat.domain.ChatMessage;
import com.example.realtimecommunication.domain.chat.domain.repository.ChatMessageRepository;
import com.example.realtimecommunication.domain.chat.dto.ChatMessageRequestDto;
import com.example.realtimecommunication.domain.chat.dto.ChatMessageResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LongPollingChatService {

    private final Map<String, List<DeferredResult<List<ChatMessageResponseDto>>>> waitingClients = new ConcurrentHashMap<>();
    private final ChatMessageRepository chatMessageRepository;

    @Value("${spring.chat.long-polling.timeout}")
    private long pollingTimeoutMillis;

    public DeferredResult<List<ChatMessageResponseDto>> waitForNewMessages(String roomId, Long lastMessageId){
        List<ChatMessageResponseDto> newMessages = getNewMessage(roomId, lastMessageId); //lastMessageId 이후로 새로운 메세지를 조회

        if(!newMessages.isEmpty()){
            return immediateResult(newMessages); //새로운 메세지가 존재한다면, 바로 반환
        }

        DeferredResult<List<ChatMessageResponseDto>> deferred = new DeferredResult<>(pollingTimeoutMillis, Collections.emptyList()); //새로운 메세지가 없다면, 30초 동안 응답을 보류, 타임 아윳 시 빈 리스트 반환

        List<DeferredResult<List<ChatMessageResponseDto>>> clientsList = getClientList(roomId);
        synchronized (clientsList){
            clientsList.add(deferred);
        }

        deferred.onCompletion(() ->
        {
            synchronized (clientsList){
                clientsList.remove(deferred);
                if(clientsList.isEmpty()){
                    waitingClients.remove(roomId);
                }
            }
        });

        return deferred; //아직 메세지가 도착하지 않았으므로, 요청은 대기상태가 됨
    }

    private void notifyClients(ChatMessage chatMessage) {
        String roomId = chatMessage.getRoomId();
        List<DeferredResult<List<ChatMessageResponseDto>>> clientsList = getClientList(roomId);

        if (clientsList.isEmpty()) return;

        ChatMessageResponseDto dto = ChatMessageResponseDto.builder()
                .id(chatMessage.getId())
                .sender(chatMessage.getSender())
                .content(chatMessage.getContent())
                .timestamp(chatMessage.getTimestamp())
                .build();

        List<ChatMessageResponseDto> dtoList = List.of(dto);

        List<DeferredResult<List<ChatMessageResponseDto>>> clientsToNotify;
        synchronized (clientsList) {
            clientsToNotify = new ArrayList<>(clientsList); // 복사
            clientsList.clear();
        }

        for (DeferredResult<List<ChatMessageResponseDto>> client : clientsToNotify) {
            client.setResult(dtoList);
        }
    }


    private List<DeferredResult<List<ChatMessageResponseDto>>> getClientList(String roomId){
        return waitingClients.computeIfAbsent(roomId, k-> Collections.synchronizedList(new ArrayList<>()));//기존 키 값이 있다면 그대로 반환, 없으면 리스트 생성 후 반환
    }

    private DeferredResult<List<ChatMessageResponseDto>> immediateResult(List<ChatMessageResponseDto> messages){
        DeferredResult<List<ChatMessageResponseDto>> result = new DeferredResult<>();
        result.setResult(messages);
        return result;
    }

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
        ChatMessage saved = ChatMessage.save(message.roomId(), message.sender(), message.content());
        chatMessageRepository.save(saved);
        notifyClients(saved);
    }
}
