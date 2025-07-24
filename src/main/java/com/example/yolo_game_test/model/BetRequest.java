package com.example.yolo_game_test.model;

import jakarta.validation.constraints.*;

public class BetRequest {
    @NotBlank
    private String playerName;

    @Min(1)
    @Max(10)
    private int number;

    @Positive
    private double amount;

    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }

    public int getNumber() { return number; }
    public void setNumber(int number) { this.number = number; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
}

