package com.distribuidora.huevos.infrastructure.persistence.repository.impl;

import com.distribuidora.huevos.domain.entities.Inventario;
import com.distribuidora.huevos.domain.repositories.InventarioRepository;
import com.distribuidora.huevos.infrastructure.persistence.mapper.InventarioJpaMapper;
import com.distribuidora.huevos.infrastructure.persistence.repository.InventarioJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public class InventarioRepositoryImpl implements InventarioRepository {

    private final InventarioJpaRepository jpaRepository;
    private final InventarioJpaMapper mapper;

    public InventarioRepositoryImpl(InventarioJpaRepository jpaRepository,
                                    InventarioJpaMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Inventario findUnico() {
        return jpaRepository.findAll().stream()
                .findFirst()
                .map(mapper::toDomain)
                .orElseThrow(() -> new IllegalStateException(
                        "No existe registro de inventario. Verifique que schema.sql se haya ejecutado."));
    }

    @Override
    public Inventario save(Inventario inventario) {
        return mapper.toDomain(jpaRepository.save(mapper.toJpa(inventario)));
    }
}
