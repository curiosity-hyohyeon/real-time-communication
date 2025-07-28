package com.example.realtimecommunication.domain.chat.service.long_polling;

import com.example.realtimecommunication.domain.chat.domain.ChatMessage;
import com.example.realtimecommunication.domain.chat.dto.ChatMessageResponseDto;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/*
 *클라이언트에서 새 메세지를 기다리는 동안 요청을 대기하고 메세지가 도착하면 알림을 주는 역할을 담당하는 컴포넌트이다.
 */

@Component
public class WaitingClientManager {
    private final Map<String, List<DeferredResult<List<ChatMessageResponseDto>>>> waitingClients = new ConcurrentHashMap<>(); //해당 roomId에서 대기 중인 클라이언트 목록

    public DeferredResult<List<ChatMessageResponseDto>> registerClient(String roomId, Long timeOut){
        DeferredResult<List<ChatMessageResponseDto>> deferred = new DeferredResult<>(timeOut, List.of()); // 클라이언트의 요청을 기다리는 객체 생성, 일정 시간 동안 시다리고 timeOut되면 빈 리스트를 반환
        List<DeferredResult<List<ChatMessageResponseDto>>> clients = getClientList(roomId); //현재 채팅 방에서 기다리고 있는 클라이언트 목록

        synchronized (clients){
            clients.add(deferred); //현재 클라이언트를 대기 목록에 추가
        }

        deferred.onCompletion(() ->{ //요청이 완료 되거나 타임 아웃이 됬다면
            synchronized (clients){
                clients.remove(deferred);  //대기 중인 클라이언트 목록 제거
                if(clients.isEmpty()){ //목록에 대기 중인 클라이언트가 없다면
                    waitingClients.remove(roomId); //해당 채팅방의 대기 목록을 제거
                }
            }
        });
        return deferred;
    }

    public void notifyClients(ChatMessage message){ //메세지를 보내면, 대기 중인 클라이언트에게 알림 전송
        String roomId = message.getRoomId(); //메세지를 보낸 채팅방의 roomId를 가져옴
        List<DeferredResult<List<ChatMessageResponseDto>>> clients = getClientList(roomId); //현재 대기 중인 클라이언트의 목록

        if(clients.isEmpty()) return; //만약 대기 중인 클라이언트가 없다면 return

        ChatMessageResponseDto dto = ChatMessageResponseDto.from(message); //DB에 저장된 엔티티를 ChatMessageResponseDto로 변환
        List<ChatMessageResponseDto> dtoList = List.of(dto); //추후 여러 메세지를 보낼 수 있게 List로 감싸기


        /*현재 toNotify list를 만들고 clients를 복사 후 clients를 비유는 이유는 다음과 같다.
        클라이언트에게 알림을 보내는 동안에 새로운 클라이언트가 들어오게 된다면, 전에 있던 대기 목록을 없애야 한다.
        그렇기 때문에 안전하게 현재 대기 중인 클라이언트의 값을 복사하고 원본 값을 삭제 함으로써, 한 메세지에 대해 한 번만 클라이언트에게 전송할 수 있게 된다.
        */
        List<DeferredResult<List<ChatMessageResponseDto>>> toNotify;
        synchronized (clients){
            toNotify = new ArrayList<>(clients);
            clients.clear();
        }

        for(DeferredResult<List<ChatMessageResponseDto>> client : toNotify){
            client.setResult(dtoList); //복사한 리스트에 있는 클라이언트에게 메세지를 전송한다. 이 시점에서 HTTP 연결을 끊기게 된다.
        }
    }

    private List<DeferredResult<List<ChatMessageResponseDto>>> getClientList(String roomId){
        return waitingClients.computeIfAbsent(roomId, k -> Collections.synchronizedList(new ArrayList<>())); //현재 대지 중인 클라이언트가 있다면 값 반환, 새로운 채팅 방(대기 중인 클라이언트가 없다면)이라면 빈 리스트를 반환한다.
    }
}
