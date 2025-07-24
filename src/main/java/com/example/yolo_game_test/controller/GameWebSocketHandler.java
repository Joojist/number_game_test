package com.example.yolo_game_test.controller;

import com.example.yolo_game_test.model.BetRequest;
import com.example.yolo_game_test.service.GameService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class GameWebSocketHandler implements WebSocketHandler {

    private final GameService gameService;
    private final ObjectMapper objectMapper;
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    private final ConcurrentHashMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    public GameWebSocketHandler(GameService gameService, ObjectMapper objectMapper) {
        this.gameService = gameService;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.put(session.getId(), session);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {
        String payload = message.getPayload().toString();
        try {
            // single bet
            BetRequest bet = objectMapper.readValue(payload, BetRequest.class);
            processBet(session, bet);
        } catch (Exception singleEx) {
            try {
                // array of bets
                List<BetRequest> bets = objectMapper.readValue(payload, new TypeReference<List<BetRequest>>() {});
                for (BetRequest bet : bets) {
                    processBet(session, bet);
                }
            } catch (Exception arrayEx) {
                try {
                    session.sendMessage(new TextMessage("Error parsing bet(s): " + arrayEx.getMessage()));
                } catch (Exception ignored) {}
            }
        }
    }

    private void processBet(WebSocketSession session, BetRequest bet) throws Exception {
        var violations = validator.validate(bet);
        if (!violations.isEmpty()) {
            session.sendMessage(new TextMessage("Invalid bet: " + violations.iterator().next().getMessage()));
            return;
        }
        gameService.placeBet(session, bet);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session.getId());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        System.err.println("Transport error on session " + session.getId() + ": " + exception.getMessage());
    }

    public ConcurrentHashMap<String, WebSocketSession> getSessions() {
        return sessions;
    }
}
