# Procurement Backend - Real-time Queue Tracker (SIH)

Spring Boot + PostgreSQL + Redis pub/sub + WebSocket backend for the
farmer procurement token/queue tracker.

## Flow

Operator marks a token complete/processing
  -> REST API updates PostgreSQL
  -> QueueService publishes a JSON event to a Redis channel
  -> QueueEventListener picks it up
  -> broadcasts over WebSocket to /topic/queue/{centreId}
  -> every subscribed farmer/admin dashboard updates instantly, no polling.

## 1. Prerequisites

- JDK 17
- Maven (or use the IDE's built-in Maven)
- Docker Desktop (for Postgres + Redis - no manual install needed)

## 2. Start Postgres and Redis

```bash
docker-compose up -d
```

This starts Postgres on `localhost:5432` (db: `procurement`, user/pass:
`postgres`/`postgres`) and Redis on `localhost:6379`. Matches
`application.yml` already - no config changes needed.

## 3. Run the app

```bash
mvn spring-boot:run
```

Tables (`centres`, `tokens`) are auto-created by Hibernate
(`ddl-auto: update`) on first run.

## 4. Test with curl (do this before touching the frontend)

Create a centre:
```bash
curl -X POST http://localhost:8080/api/centres \
  -H "Content-Type: application/json" \
  -d '{"name":"Haridwar Centre","code":"HPC-001","operatingHours":"08:00-17:00"}'
```
Note the `id` returned - use it below as `{centreId}`.

Create a token (farmer registers):
```bash
curl -X POST http://localhost:8080/api/centres/1/tokens \
  -H "Content-Type: application/json" \
  -d '{"farmerName":"Ramesh Kumar","quantityQuintal":35}'
```

Operator starts processing a token:
```bash
curl -X PATCH http://localhost:8080/api/tokens/1/process
```

Operator completes a token:
```bash
curl -X PATCH http://localhost:8080/api/tokens/1/complete
```

Check current queue (REST fallback):
```bash
curl http://localhost:8080/api/centres/1/queue
```

## 5. Connect the frontend (SockJS + STOMP)

Add to `farmer.html` / `operator.html`, replacing the fake
`setInterval(updateQueue, 5000)` polling:

```html
<script src="https://cdnjs.cloudflare.com/ajax/libs/sockjs-client/1.6.1/sockjs.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/stomp.js/2.3.3/stomp.min.js"></script>
<script>
const centreId = 1; // get this from the logged-in farmer/operator context

const socket = new SockJS('http://localhost:8080/ws');
const stompClient = Stomp.over(socket);

stompClient.connect({}, () => {
  stompClient.subscribe('/topic/queue/' + centreId, (message) => {
    const event = JSON.parse(message.body);
    // event = { centreId, currentlyProcessingToken, waitingCount, message }
    document.getElementById('currentToken').textContent = event.currentlyProcessingToken;
    document.getElementById('ahead').textContent = event.waitingCount;
    // update progress bar / wait time from these two numbers, same as before
  });
});
</script>
```

That's it - no more fake `setInterval`. The moment the operator clicks
"Complete" in `operator.html`, this callback fires in every open
`farmer.html` tab.

## 6. Next features to layer on (once this works end-to-end)

- Dynamic wait-time estimate: store actual processing duration per
  token, average the last N per centre instead of the hardcoded
  6 min/farmer.
- Duplicate-farmer / fraud check: reject a `createToken` if the same
  farmer mobile already has an open (WAITING/PROCESSING) token at any
  centre today.
- SMS fallback: on `TOKEN_PROCESSING` with 1-2 farmers left ahead,
  trigger an SMS via a gateway API using the farmer's stored mobile
  number.

## Project structure

```
src/main/java/com/sih/procurement/
  ProcurementApplication.java   - entry point
  config/
    WebSocketConfig.java        - STOMP endpoint + broker setup
    RedisConfig.java            - Redis topic + listener container
  entity/
    Centre.java, Token.java, TokenStatus.java
  repository/
    CentreRepository.java, TokenRepository.java
  dto/
    QueueEvent.java             - the object broadcast to clients
    CreateTokenRequest.java
  service/
    QueueService.java           - business logic + Redis publish
  listener/
    QueueEventListener.java     - Redis -> WebSocket bridge
  controller/
    TokenController.java, CentreController.java
```
