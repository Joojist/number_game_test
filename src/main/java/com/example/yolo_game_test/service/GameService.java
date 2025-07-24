package com.example.yolo_game_test.service;

import com.example.yolo_game_test.controller.GameWebSocketHandler;
import com.example.yolo_game_test.model.BetRequest;
import com.example.yolo_game_test.model.ResultMessage;
import com.example.yolo_game_test.model.WinningPlayer;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.*;
import java.util.concurrent.*;

@Service
public class GameService {

    private final GameWebSocketHandler handler;
    private final ObjectMapper objectMapper;
    private final List<Bet> currentBets = new CopyOnWriteArrayList<>();
    private final ScheduledExecutorService executor;
    private final ConcurrentHashMap<String, Double> totalWinningsMap = new ConcurrentHashMap<>();

    private static class Bet {
        final WebSocketSession session;
        final BetRequest request;
        Bet(WebSocketSession session, BetRequest request) {
            this.session = session;
            this.request = request;
        }
    }

    public GameService(@Lazy GameWebSocketHandler handler, ObjectMapper objectMapper, ScheduledExecutorService executor) {
        this.handler = handler;
        this.objectMapper = objectMapper;
        this.executor = executor;
    }

    public void placeBet(WebSocketSession session, BetRequest request) {
        currentBets.add(new Bet(session, request));
    }

    @PostConstruct
    public void startGameLoop() {
        executor.scheduleAtFixedRate(this::runGameRound, 10, 10, TimeUnit.SECONDS);
    }

    private void runGameRound() {
        int winningNumber = ThreadLocalRandom.current().nextInt(1, 11);
        List<WinningPlayer> winners = new ArrayList<>();

        for (Bet bet : currentBets) {
            try {
                String player = bet.request.getPlayerName();
                double amount = bet.request.getAmount();
                boolean won = bet.request.getNumber() == winningNumber;
                double winnings = won ? amount * 9.9 : 0;

                if (won) {
                    totalWinningsMap.merge(player, winnings, Double::sum);
                    winners.add(new WinningPlayer(player, winnings));
                }

                double totalWinnings = totalWinningsMap.getOrDefault(player, 0.0);

                ResultMessage result = new ResultMessage(
                        won ? "You won!" : "You lost!",
                        won,
                        winnings,
                        totalWinnings
                );

                bet.session.sendMessage(new TextMessage(objectMapper.writeValueAsString(result)));

            } catch (Exception e) {
                System.err.println("error sending result to session " + bet.session.getId() + ": " + e.getMessage());
            }
        }

        try {
            Map<String, Object> broadcastMessage = new HashMap<>();
            broadcastMessage.put("type", "roundResult");
            broadcastMessage.put("winningNumber", winningNumber);
            broadcastMessage.put("winners", winners);

            String json = objectMapper.writeValueAsString(broadcastMessage);

            for (WebSocketSession session : handler.getSessions().values()) {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(json));
                }
            }
        } catch (Exception e) {
            System.err.println("error broadcasting round result: " + e.getMessage());
        }

        currentBets.clear();
    }
}
