package com.distribuidora.huevos.infrastructure.persistence.repository.impl;

import com.distribuidora.huevos.domain.entities.CargaSaldo;
import com.distribuidora.huevos.domain.repositories.CargaSaldoRepository;
import com.distribuidora.huevos.domain.valueobjects.Dinero;
import com.distribuidora.huevos.infrastructure.persistence.entity.CargaSaldoJpaEntity;
import com.distribuidora.huevos.infrastructure.persistence.repository.CargaSaldoJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CargaSaldoRepositoryImpl implements CargaSaldoRepository {

    private final CargaSaldoJpaRepository jpaRepository;

    public CargaSaldoRepositoryImpl(CargaSaldoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public CargaSaldo save(CargaSaldo cs) {
        return toDomain(jpaRepository.save(toEntity(cs)));
    }

    @Override
    public List<CargaSaldo> findByClienteIdOrderByFechaAsc(Long clienteId) {
        return jpaRepository.findByClienteIdOrderByFechaAsc(clienteId).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public void deleteByClienteId(Long clienteId) {
        jpaRepository.deleteByClienteId(clienteId);
    }

    // ── mappers privados ──────────────────────────────────────────────────────

    private CargaSaldoJpaEntity toEntity(CargaSaldo cs) {
        CargaSaldoJpaEntity e = new CargaSaldoJpaEntity();
        e.setId(cs.getId());
        e.setClienteId(cs.getClienteId());
        e.setMonto(cs.getMonto().getValor());
        e.setDescripcion(cs.getDescripcion());
        e.setFecha(cs.getFecha());
        return e;
    }

    private CargaSaldo toDomain(CargaSaldoJpaEntity e) {
        return new CargaSaldo(
                e.getId(),
                e.getClienteId(),
                Dinero.de(e.getMonto()),
                e.getDescripcion(),
                e.getFecha());
    }
}
