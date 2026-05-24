package com.distribuidora.huevos.infrastructure.persistence.repository.impl;

import com.distribuidora.huevos.domain.entities.Caja;
import com.distribuidora.huevos.domain.repositories.CajaRepository;
import com.distribuidora.huevos.infrastructure.persistence.mapper.CajaJpaMapper;
import com.distribuidora.huevos.infrastructure.persistence.repository.CajaJpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public class CajaRepositoryImpl implements CajaRepository {

    private final CajaJpaRepository jpaRepository;
    private final CajaJpaMapper mapper;

    public CajaRepositoryImpl(CajaJpaRepository jpaRepository, CajaJpaMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Caja> findByFecha(LocalDate fecha) {
        return jpaRepository.findByFecha(fecha).map(mapper::toDomain);
    }

    @Override
    public Caja save(Caja caja) {
        return mapper.toDomain(jpaRepository.save(mapper.toJpa(caja)));
    }
}
