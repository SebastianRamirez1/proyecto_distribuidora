package com.distribuidora.huevos.infrastructure.persistence.repository;

import com.distribuidora.huevos.infrastructure.persistence.entity.InventarioJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InventarioJpaRepository extends JpaRepository<InventarioJpaEntity, Long> {
    /** Siempre retorna la misma fila (la de menor id) — evita ambigüedad si hubiera duplicados. */
    Optional<InventarioJpaEntity> findFirstByOrderByIdAsc();
}
