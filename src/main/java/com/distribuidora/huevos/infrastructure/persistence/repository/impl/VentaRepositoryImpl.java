package com.distribuidora.huevos.infrastructure.persistence.repository.impl;

import com.distribuidora.huevos.domain.entities.Venta;
import com.distribuidora.huevos.domain.repositories.VentaRepository;
import com.distribuidora.huevos.infrastructure.persistence.mapper.VentaJpaMapper;
import com.distribuidora.huevos.infrastructure.persistence.repository.VentaJpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class VentaRepositoryImpl implements VentaRepository {

    private final VentaJpaRepository jpaRepository;
    private final VentaJpaMapper mapper;

    public VentaRepositoryImpl(VentaJpaRepository jpaRepository, VentaJpaMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Venta save(Venta venta) {
        return mapper.toDomain(jpaRepository.save(mapper.toJpa(venta)));
    }

    @Override
    public List<Venta> findByFecha(LocalDate fecha) {
        return jpaRepository.findByFecha(fecha).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
