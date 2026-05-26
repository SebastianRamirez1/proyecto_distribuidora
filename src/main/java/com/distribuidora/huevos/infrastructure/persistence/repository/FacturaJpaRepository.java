package com.distribuidora.huevos.infrastructure.persistence.repository;

import com.distribuidora.huevos.infrastructure.persistence.entity.FacturaJpaEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FacturaJpaRepository extends JpaRepository<FacturaJpaEntity, Long> {

    Optional<FacturaJpaEntity> findByVentaId(Long ventaId);

    @Query("SELECT f FROM FacturaJpaEntity f ORDER BY f.fechaEmision DESC")
    List<FacturaJpaEntity> findAllOrderByFechaDesc();

    @Query("SELECT f FROM FacturaJpaEntity f WHERE f.clienteId = :clienteId ORDER BY f.fechaEmision DESC")
    List<FacturaJpaEntity> findByClienteIdOrderByFechaDesc(@Param("clienteId") Long clienteId);

    @Modifying
    @Transactional
    @Query("DELETE FROM FacturaJpaEntity f WHERE f.clienteId = :clienteId")
    void deleteByClienteId(@Param("clienteId") Long clienteId);
}
