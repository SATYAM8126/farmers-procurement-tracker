package com.sih.procurement.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sih.procurement.config.RedisConfig;
import com.sih.procurement.dto.CreateTokenRequest;
import com.sih.procurement.dto.QueueEvent;
import com.sih.procurement.entity.Centre;
import com.sih.procurement.entity.Token;
import com.sih.procurement.entity.TokenStatus;
import com.sih.procurement.repository.CentreRepository;
import com.sih.procurement.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class QueueService {

    private final TokenRepository tokenRepository;
    private final CentreRepository centreRepository;
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public Token createToken(Long centreId, CreateTokenRequest request) {
        Centre centre = centreRepository.findById(centreId)
                .orElseThrow(() -> new IllegalArgumentException("Centre not found: " + centreId));

        int nextTokenNumber = tokenRepository.findTopByCentreIdOrderByTokenNumberDesc(centreId)
                .map(t -> t.getTokenNumber() + 1)
                .orElse(1);

        Token token = new Token();
        token.setCentre(centre);
        token.setTokenNumber(nextTokenNumber);
        token.setFarmerName(request.getFarmerName());
        token.setQuantityQuintal(request.getQuantityQuintal());
        token.setStatus(TokenStatus.WAITING);

        Token saved = tokenRepository.save(token);
        publishQueueState(centreId, "TOKEN_CREATED");
        return saved;
    }

    @Transactional
    public Token processToken(Long tokenId) {
        Token token = getTokenOrThrow(tokenId);
        token.setStatus(TokenStatus.PROCESSING);
        token.setProcessingStartedAt(LocalDateTime.now());
        Token saved = tokenRepository.save(token);
        publishQueueState(token.getCentre().getId(), "TOKEN_PROCESSING");
        return saved;
    }

    @Transactional
    public Token completeToken(Long tokenId) {
        Token token = getTokenOrThrow(tokenId);
        token.setStatus(TokenStatus.COMPLETED);
        token.setCompletedAt(LocalDateTime.now());
        Token saved = tokenRepository.save(token);
        publishQueueState(token.getCentre().getId(), "TOKEN_COMPLETED");
        return saved;
    }

    private Token getTokenOrThrow(Long tokenId) {
        return tokenRepository.findById(tokenId)
                .orElseThrow(() -> new IllegalArgumentException("Token not found: " + tokenId));
    }

    // Builds the current snapshot of a centre's queue and publishes it to Redis.
    // Every dashboard (farmer + operator + admin) reacts to this same event.
    private void publishQueueState(Long centreId, String message) {
        Integer currentToken = tokenRepository
                .findTopByCentreIdAndStatusOrderByProcessingStartedAtDesc(centreId, TokenStatus.PROCESSING)
                .map(Token::getTokenNumber)
                .orElse(0);

        long waitingCount = tokenRepository.countByCentreIdAndStatus(centreId, TokenStatus.WAITING);

        QueueEvent event = new QueueEvent(centreId, currentToken, waitingCount, message);

        try {
            String json = objectMapper.writeValueAsString(event);
            stringRedisTemplate.convertAndSend(RedisConfig.QUEUE_CHANNEL, json);
        } catch (Exception e) {
            throw new RuntimeException("Failed to publish queue event", e);
        }
    }
}
