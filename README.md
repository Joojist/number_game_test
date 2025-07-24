

**YOLO Game Server - WebSocket Betting Game**

A simple WebSocket-based number betting game built with Spring Boot. Players bet on a number from 1 to 10. Every 10 seconds, a random winning number is drawn. If the player's number matches, they win 9.9× their bet. The game also tracks total winnings per player name for the session.

---

### Features

* Real-time WebSocket communication
* Players bet on numbers 1–10
* 10-second automatic game rounds
* Each winning number is broadcast to all players
* Players receive a personal result message each round
* Total winnings are tracked per player (session-based) – added for fun statistics

---

### Technologies

* Java 17+
* Spring Boot 3
* WebSocket (no STOMP)
* Jackson (JSON)
* Concurrent data structures (thread-safe)

---

### How to Run

1. Make sure you have Java 17+ installed.

2. Clone and navigate into the project directory:

   ```bash
   git clone https://github.com/Joojist/yolo_game_test.git
   cd yolo-game-server
   ```

3. Run the app:

   ```bash
   ./gradlew bootRun
   ```

4. The server runs on:

   * HTTP: `http://localhost:8080`
   * WebSocket endpoint: `ws://localhost:8080/ws/game`

---

### WebSocket API

You can test the game using one of the following tools:

#### ✅ Best Option: Postman (v10+)

> I personally used Postman, and it's the most convenient tool for WebSocket testing.

1. Open Postman > **New > WebSocket Request**
2. Connect to: `ws://localhost:8080/ws/game`
3. Send a raw JSON message like this:

```json
{
  "playerName": "Alice",
  "number": 5,
  "amount": 10.0
}
```

#### Web-Based Alternatives

If you prefer not to install anything, you can use:

* [websocketking.com](https://websocketking.com)
* [piehost.com/websocket-tester](https://piehost.com/websocket-tester)

Simply open the page, connect to `ws://localhost:8080/ws/game`, and send JSON messages.

---

### Sending a Bet

You can send either a **single bet object** or a **list of bets**:

#### Single bet

```json
{
  "playerName": "Alice",
  "number": 5,
  "amount": 10.0
}
```

#### Multiple bets

```json
[
  { "playerName": "Bob", "number": 3, "amount": 5.0 },
  { "playerName": "Carol", "number": 7, "amount": 15.0 }
]
```

* `playerName`: Your name
* `number`: Your chosen number (1–10)
* `amount`: The amount you want to bet

---

### Individual Result Message

After each round, each player receives their personal result:

```json
{
  "message": "You won!",
  "won": true,
  "amount": 118.8,
  "totalWinnings": 237.6
}
```

* `message`: "You won!" or "You lost!"
* `won`: Boolean indicating if you won
* `amount`: Winnings this round (0 if lost)
* `totalWinnings`: Your total winnings so far

---

### Round Result Broadcast

All clients receive a summary after each round:

```json
{
  "type": "roundResult",
  "winningNumber": 2,
  "winners": [
    { "playerName": "Diana", "amountWon": 49.5 },
    { "playerName": "Kevin", "amountWon": 118.8 }
  ]
}
```

* `type`: Always `"roundResult"`
* `winningNumber`: The lucky number (1–10)
* `winners`: List of players who won and their amounts

---

### Notes

* RTP (return to player) is \~99% due to the 9.9× payout on a 1-in-10 chance
* Total winnings are **not persisted** — they reset on server restart
* You can simulate rounds quickly by programmatically placing many bets
* This is an in-memory demo, not intended for production or real money
