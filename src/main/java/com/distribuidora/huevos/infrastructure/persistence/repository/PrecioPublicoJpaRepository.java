package com.distribuidora.huevos.infrastructure.persistence.repository;

import com.distribuidora.huevos.infrastructure.persistence.entity.PrecioPublicoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PrecioPublicoJpaRepository extends JpaRepository<PrecioPublicoJpaEntity, Long> {
    /** Siempre retorna la misma fila (la de menor id) — evita ambigüedad si hubiera duplicados. */
    Optional<PrecioPublicoJpaEntity> findFirstByOrderByIdAsc();
}
