package com.sih.procurement.repository;

import com.sih.procurement.entity.Centre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CentreRepository extends JpaRepository<Centre, Long> {
    Optional<Centre> findByCode(String code);
}
