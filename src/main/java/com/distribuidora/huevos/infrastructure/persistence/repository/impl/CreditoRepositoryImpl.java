package com.distribuidora.huevos.infrastructure.persistence.repository.impl;

import com.distribuidora.huevos.domain.entities.Credito;
import com.distribuidora.huevos.domain.repositories.CreditoRepository;
import com.distribuidora.huevos.infrastructure.persistence.mapper.CreditoJpaMapper;
import com.distribuidora.huevos.infrastructure.persistence.repository.CreditoJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class CreditoRepositoryImpl implements CreditoRepository {

    private final CreditoJpaRepository jpaRepository;
    private final CreditoJpaMapper mapper;

    public CreditoRepositoryImpl(CreditoJpaRepository jpaRepository, CreditoJpaMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Credito> findByClienteId(Long clienteId) {
        return jpaRepository.findByClienteId(clienteId).map(mapper::toDomain);
    }

    @Override
    public Credito save(Credito credito) {
        return mapper.toDomain(jpaRepository.save(mapper.toJpa(credito)));
    }
}
