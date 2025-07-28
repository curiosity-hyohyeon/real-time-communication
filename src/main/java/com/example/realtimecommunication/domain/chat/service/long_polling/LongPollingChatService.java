package com.example.realtimecommunication.domain.chat.service.long_polling;

import com.example.realtimecommunication.domain.chat.domain.ChatMessage;
import com.example.realtimecommunication.domain.chat.dto.ChatMessageRequestDto;
import com.example.realtimecommunication.domain.chat.dto.ChatMessageResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LongPollingChatService {
    private final ChatMessageService chatMessageService;
    private final WaitingClientManager waitingClientManager;

    @Value("${spring.chat.long-polling.timeout}")
    private long pollingTimeoutMillis;

    public DeferredResult<List<ChatMessageResponseDto>> waitForNewMessage(String roomId, Long lastMessageId){
        List<ChatMessageResponseDto> newMessage = chatMessageService.getNewMessages(roomId, lastMessageId); //현재 메세지가 왔는지 확인
        if(!newMessage.isEmpty()){
            return immediateResult(newMessage); //새 메세지가 왔다면 즉시 클라이언트에게 반환
        }

        return waitingClientManager.registerClient(roomId, pollingTimeoutMillis); //아니라면 대기 목록에 추가하고 지정 시간 동안 대기 상태에 들어감
    }

    public void handleNewMessage(ChatMessageRequestDto dto){
        ChatMessage saved = chatMessageService.saveMessage(dto); //요청한 메세지를 저장함
        waitingClientManager.notifyClients(saved); //메세지가 왔다고 클라이언트에게 알림
    }

    private DeferredResult<List<ChatMessageResponseDto>> immediateResult(List<ChatMessageResponseDto> message){
        DeferredResult<List<ChatMessageResponseDto>> result = new DeferredResult<>(); //요청을 기다리는 객체 생성
        result.setResult(message); //바로 반환
        return result;
    }
}
