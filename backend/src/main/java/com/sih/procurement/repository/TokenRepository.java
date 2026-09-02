package com.sih.procurement.repository;

import com.sih.procurement.entity.Token;
import com.sih.procurement.entity.TokenStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {

    List<Token> findByCentreIdOrderByTokenNumberAsc(Long centreId);

    List<Token> findByCentreIdAndStatusOrderByTokenNumberAsc(Long centreId, TokenStatus status);

    Optional<Token> findTopByCentreIdAndStatusOrderByProcessingStartedAtDesc(Long centreId, TokenStatus status);

    long countByCentreIdAndStatus(Long centreId, TokenStatus status);

    // Used to auto-increment the human-facing token number per centre
    Optional<Token> findTopByCentreIdOrderByTokenNumberDesc(Long centreId);
}
