package com.distribuidora.huevos.infrastructure.persistence.repository;

import com.distribuidora.huevos.infrastructure.persistence.entity.AbonoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AbonoJpaRepository extends JpaRepository<AbonoJpaEntity, Long> {

    List<AbonoJpaEntity> findByClienteIdOrderByFechaDesc(Long clienteId);
}
