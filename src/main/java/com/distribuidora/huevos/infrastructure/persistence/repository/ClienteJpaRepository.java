package com.distribuidora.huevos.infrastructure.persistence.repository;

import com.distribuidora.huevos.infrastructure.persistence.entity.ClienteJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteJpaRepository extends JpaRepository<ClienteJpaEntity, Long> {
}
