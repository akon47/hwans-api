package com.hwans.apiserver.service.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hwans.apiserver.dto.websocket.MessageDto;
import com.hwans.apiserver.dto.websocket.MessageType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebSocketServiceImpl extends TextWebSocketHandler implements WebSocketService {
    private Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        var sessionId = session.getId();
        sessions.put(sessionId, session);

        NotifySessionCountChanged();
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        var sessionId = session.getId();
        sessions.remove(sessionId);

        NotifySessionCountChanged();
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {

    }

    private void NotifySessionCountChanged() {
        var message = MessageDto.builder().type(MessageType.SESSION_COUNT_CHANGED).payload(sessions.size()).build();
        var objectMapper = new ObjectMapper();

        sessions.values().forEach(session -> {
            try {
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
            } catch (Exception e) {
                log.info("NotifySessionCountChanged failed");
                log.trace("NotifySessionCountChanged failed trace: {}", e);
            }
        });
    }

    @Override
    public WebSocketHandler getWebSocketHandler() {
        return this;
    }
}
