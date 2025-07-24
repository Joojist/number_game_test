package com.example.yolo_game_test.model;

public class WinningPlayer {
    private final String playerName;
    private final double amountWon;

    public WinningPlayer(String playerName, double amountWon) {
        this.playerName = playerName;
        this.amountWon = amountWon;
    }

    public String getPlayerName() { return playerName; }
    public double getAmountWon() { return amountWon; }
}

