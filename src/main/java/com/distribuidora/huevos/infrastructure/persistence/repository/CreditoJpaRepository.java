package com.distribuidora.huevos.infrastructure.persistence.repository;

import com.distribuidora.huevos.infrastructure.persistence.entity.CreditoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CreditoJpaRepository extends JpaRepository<CreditoJpaEntity, Long> {

    Optional<CreditoJpaEntity> findByClienteId(Long clienteId);
}
