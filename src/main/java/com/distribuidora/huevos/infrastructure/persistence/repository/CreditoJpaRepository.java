package com.distribuidora.huevos.infrastructure.persistence.repository;

import com.distribuidora.huevos.infrastructure.persistence.entity.CreditoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CreditoJpaRepository extends JpaRepository<CreditoJpaEntity, Long> {

    Optional<CreditoJpaEntity> findByClienteId(Long clienteId);

    @Query("SELECT c FROM CreditoJpaEntity c WHERE c.montoTotal > c.montoPagado ORDER BY (c.montoTotal - c.montoPagado) DESC")
    List<CreditoJpaEntity> findDeudores();
}
