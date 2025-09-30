package kopo.motionservice.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
@Component
public class MotionHandler extends TextWebSocketHandler {

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("[MotionHandler] New client connected: {}", session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        log.info("[MotionHandler] Received data from client {}: {}", session.getId(), message.getPayload());
        // Echo back the received message
        session.sendMessage(new TextMessage("ECHO: " + message.getPayload()));
    }
}
