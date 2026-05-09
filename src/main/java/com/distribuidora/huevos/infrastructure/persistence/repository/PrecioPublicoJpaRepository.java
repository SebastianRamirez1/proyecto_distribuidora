package com.distribuidora.huevos.infrastructure.persistence.repository;

import com.distribuidora.huevos.infrastructure.persistence.entity.PrecioPublicoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrecioPublicoJpaRepository extends JpaRepository<PrecioPublicoJpaEntity, Long> {
}
