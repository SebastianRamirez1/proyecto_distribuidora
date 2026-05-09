package com.distribuidora.huevos.infrastructure.persistence.repository;

import com.distribuidora.huevos.infrastructure.persistence.entity.CajaJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface CajaJpaRepository extends JpaRepository<CajaJpaEntity, Long> {

    Optional<CajaJpaEntity> findByFecha(LocalDate fecha);
}
