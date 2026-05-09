package com.distribuidora.huevos.infrastructure.persistence.repository.impl;

import com.distribuidora.huevos.domain.repositories.PrecioPublicoRepository;
import com.distribuidora.huevos.domain.valueobjects.Precio;
import com.distribuidora.huevos.domain.valueobjects.PrecioPublico;
import com.distribuidora.huevos.infrastructure.persistence.entity.PrecioPublicoJpaEntity;
import com.distribuidora.huevos.infrastructure.persistence.repository.PrecioPublicoJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public class PrecioPublicoRepositoryImpl implements PrecioPublicoRepository {

    private final PrecioPublicoJpaRepository jpaRepository;

    public PrecioPublicoRepositoryImpl(PrecioPublicoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public PrecioPublico findCurrent() {
        return jpaRepository.findAll().stream()
                .findFirst()
                .map(e -> new PrecioPublico(e.getId(),
                        Precio.de(e.getPrecioExtra()),
                        Precio.de(e.getPrecioNormal())))
                .orElseThrow(() -> new IllegalStateException(
                        "No existe precio público configurado. Verifique que schema.sql se haya ejecutado."));
    }

    @Override
    public PrecioPublico save(PrecioPublico precioPublico) {
        PrecioPublicoJpaEntity entity = new PrecioPublicoJpaEntity();
        entity.setId(precioPublico.getId());
        entity.setPrecioExtra(precioPublico.getPrecioExtra().getValor());
        entity.setPrecioNormal(precioPublico.getPrecioNormal().getValor());
        PrecioPublicoJpaEntity saved = jpaRepository.save(entity);
        return new PrecioPublico(saved.getId(),
                Precio.de(saved.getPrecioExtra()),
                Precio.de(saved.getPrecioNormal()));
    }
}
