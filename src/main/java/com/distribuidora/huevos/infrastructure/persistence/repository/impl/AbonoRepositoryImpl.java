package com.distribuidora.huevos.infrastructure.persistence.repository.impl;

import com.distribuidora.huevos.domain.entities.Abono;
import com.distribuidora.huevos.domain.repositories.AbonoRepository;
import com.distribuidora.huevos.domain.valueobjects.Dinero;
import com.distribuidora.huevos.infrastructure.persistence.entity.AbonoJpaEntity;
import com.distribuidora.huevos.infrastructure.persistence.repository.AbonoJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AbonoRepositoryImpl implements AbonoRepository {

    private final AbonoJpaRepository jpaRepository;

    public AbonoRepositoryImpl(AbonoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Abono save(Abono abono) {
        AbonoJpaEntity entity = toEntity(abono);
        AbonoJpaEntity saved  = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public List<Abono> findByClienteIdOrderByFechaDesc(Long clienteId) {
        return jpaRepository.findByClienteIdOrderByFechaDesc(clienteId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    // ── mappers privados ──────────────────────────────────────────────────────

    private AbonoJpaEntity toEntity(Abono a) {
        AbonoJpaEntity e = new AbonoJpaEntity();
        e.setId(a.getId());
        e.setClienteId(a.getClienteId());
        e.setMonto(a.getMonto().getValor());
        e.setMedioPago(a.getMedioPago());
        e.setFecha(a.getFecha());
        return e;
    }

    private Abono toDomain(AbonoJpaEntity e) {
        return new Abono(
                e.getId(),
                e.getClienteId(),
                Dinero.de(e.getMonto()),
                e.getMedioPago(),
                e.getFecha()
        );
    }
}
