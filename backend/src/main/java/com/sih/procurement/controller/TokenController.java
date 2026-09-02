package com.sih.procurement.controller;

import com.sih.procurement.dto.CreateTokenRequest;
import com.sih.procurement.entity.Token;
import com.sih.procurement.repository.TokenRepository;
import com.sih.procurement.service.QueueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // tighten this before real deployment
public class TokenController {

    private final QueueService queueService;
    private final TokenRepository tokenRepository;

    @PostMapping("/centres/{centreId}/tokens")
    public ResponseEntity<Token> createToken(
            @PathVariable Long centreId,
            @Valid @RequestBody CreateTokenRequest request) {
        return ResponseEntity.ok(queueService.createToken(centreId, request));
    }

    @PatchMapping("/tokens/{tokenId}/process")
    public ResponseEntity<Token> processToken(@PathVariable Long tokenId) {
        return ResponseEntity.ok(queueService.processToken(tokenId));
    }

    @PatchMapping("/tokens/{tokenId}/complete")
    public ResponseEntity<Token> completeToken(@PathVariable Long tokenId) {
        return ResponseEntity.ok(queueService.completeToken(tokenId));
    }

    // REST fallback - useful for initial page load before the WebSocket
    // connects, or if a client can't hold a socket open
    @GetMapping("/centres/{centreId}/queue")
    public ResponseEntity<List<Token>> getQueue(@PathVariable Long centreId) {
        return ResponseEntity.ok(tokenRepository.findByCentreIdOrderByTokenNumberAsc(centreId));
    }
}
