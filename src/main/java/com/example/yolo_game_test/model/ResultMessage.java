package com.example.yolo_game_test.model;

public class ResultMessage {
    private final String message;
    private final boolean won;
    private final double amount;
    private final double totalWinnings;

    public ResultMessage(String message, boolean won, double amount, double totalWinnings) {
        this.message = message;
        this.won = won;
        this.amount = amount;
        this.totalWinnings = totalWinnings;
    }

    public String getMessage() { return message; }

    public boolean isWon() { return won; }

    public double getAmount() { return amount; }

    public double getTotalWinnings() { return totalWinnings; }
}
