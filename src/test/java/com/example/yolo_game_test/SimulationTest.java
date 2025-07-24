package com.example.yolo_game_test;

import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.DoubleAdder;

public class SimulationTest {

    @Test
    void simulateMillionGames() throws InterruptedException {
        int games = 1_000_000;
        double betAmount = 1.0;
        int threads = 24;

        AtomicLong totalBet = new AtomicLong();
        DoubleAdder totalWon = new DoubleAdder();

        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(games);
        ThreadLocal<Random> threadLocalRandom = ThreadLocal.withInitial(Random::new);

        for (int i = 0; i < games; i++) {
            executor.execute(() -> {
                try {
                    Random random = threadLocalRandom.get();
                    int playerBet = random.nextInt(10) + 1;
                    int winningNumber = random.nextInt(10) + 1;

                    totalBet.incrementAndGet();

                    if (playerBet == winningNumber) {
                        totalWon.add(betAmount * 9.9);
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        double totalBetAmount = totalBet.get() * betAmount;
        double totalWonAmount = totalWon.sum();
        double rtp = totalWonAmount / totalBetAmount;

        System.out.printf("Total bet: %.0f, Total won: %.2f, RTP: %.2f%%%n", totalBetAmount, totalWonAmount, rtp * 100);
    }
}
