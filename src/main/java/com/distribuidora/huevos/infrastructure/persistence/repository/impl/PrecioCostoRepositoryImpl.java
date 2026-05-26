package com.distribuidora.huevos.infrastructure.persistence.repository.impl;

import com.distribuidora.huevos.domain.repositories.PrecioCostoRepository;
import com.distribuidora.huevos.domain.valueobjects.Precio;
import com.distribuidora.huevos.domain.valueobjects.PrecioCosto;
import com.distribuidora.huevos.infrastructure.persistence.entity.PrecioCostoJpaEntity;
import com.distribuidora.huevos.infrastructure.persistence.repository.PrecioCostoJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public class PrecioCostoRepositoryImpl implements PrecioCostoRepository {

    private final PrecioCostoJpaRepository jpaRepository;

    public PrecioCostoRepositoryImpl(PrecioCostoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public PrecioCosto findCurrent() {
        return jpaRepository.findFirstByOrderByIdAsc()
                .map(e -> new PrecioCosto(e.getId(),
                        Precio.de(e.getCostoExtra()),
                        Precio.de(e.getCostoAA()),
                        Precio.de(e.getCostoA()),
                        Precio.de(e.getCostoB())))
                .orElseThrow(() -> new IllegalStateException(
                        "No existe precio de costo configurado. Verifique que schema.sql se haya ejecutado."));
    }

    @Override
    public PrecioCosto save(PrecioCosto precioCosto) {
        PrecioCostoJpaEntity entity = new PrecioCostoJpaEntity();
        entity.setId(precioCosto.getId());
        entity.setCostoExtra(precioCosto.getCostoExtra().getValor());
        entity.setCostoAA(precioCosto.getCostoAA().getValor());
        entity.setCostoA(precioCosto.getCostoA().getValor());
        entity.setCostoB(precioCosto.getCostoB().getValor());
        PrecioCostoJpaEntity saved = jpaRepository.save(entity);
        return new PrecioCosto(saved.getId(),
                Precio.de(saved.getCostoExtra()),
                Precio.de(saved.getCostoAA()),
                Precio.de(saved.getCostoA()),
                Precio.de(saved.getCostoB()));
    }
}
