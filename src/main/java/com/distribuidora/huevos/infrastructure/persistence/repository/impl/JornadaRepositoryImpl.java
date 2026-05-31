package com.distribuidora.huevos.infrastructure.persistence.repository.impl;

import com.distribuidora.huevos.domain.entities.Jornada;
import com.distribuidora.huevos.domain.enums.EstadoJornada;
import com.distribuidora.huevos.domain.repositories.JornadaRepository;
import com.distribuidora.huevos.infrastructure.persistence.entity.JornadaJpaEntity;
import com.distribuidora.huevos.infrastructure.persistence.repository.JornadaJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class JornadaRepositoryImpl implements JornadaRepository {

    private final JornadaJpaRepository jpa;

    public JornadaRepositoryImpl(JornadaJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<Jornada> findActiva() {
        return jpa.findByEstado(EstadoJornada.ABIERTA).map(this::toDomain);
    }

    @Override
    public Optional<Jornada> findEnCierre() {
        return jpa.findByEstado(EstadoJornada.EN_CIERRE).map(this::toDomain);
    }

    @Override
    public Optional<Jornada> findById(Long id) {
        return jpa.findById(id).map(this::toDomain);
    }

    @Override
    public Jornada save(Jornada jornada) {
        JornadaJpaEntity entity;
        if (jornada.getId() != null) {
            entity = jpa.findById(jornada.getId()).orElse(new JornadaJpaEntity());
        } else {
            entity = new JornadaJpaEntity();
        }
        entity.setFecha(jornada.getFecha());
        entity.setEstado(jornada.getEstado());
        entity.setAbiertaEn(jornada.getAbiertaEn());
        entity.setCerradaEn(jornada.getCerradaEn());
        return toDomain(jpa.save(entity));
    }

    private Jornada toDomain(JornadaJpaEntity e) {
        return new Jornada(e.getId(), e.getFecha(), e.getEstado(), e.getAbiertaEn(), e.getCerradaEn());
    }
}
