package com.distribuidora.huevos.infrastructure.persistence.repository;

import com.distribuidora.huevos.infrastructure.persistence.entity.CargaSaldoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CargaSaldoJpaRepository extends JpaRepository<CargaSaldoJpaEntity, Long> {

    List<CargaSaldoJpaEntity> findByClienteIdOrderByFechaAsc(Long clienteId);

    void deleteByClienteId(Long clienteId);
}
