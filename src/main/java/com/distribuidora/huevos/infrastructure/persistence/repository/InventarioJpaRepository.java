package com.distribuidora.huevos.infrastructure.persistence.repository;

import com.distribuidora.huevos.infrastructure.persistence.entity.InventarioJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventarioJpaRepository extends JpaRepository<InventarioJpaEntity, Long> {
}
