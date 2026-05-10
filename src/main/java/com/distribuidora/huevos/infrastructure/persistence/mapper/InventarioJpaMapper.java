package com.distribuidora.huevos.infrastructure.persistence.mapper;

import com.distribuidora.huevos.domain.entities.Inventario;
import com.distribuidora.huevos.infrastructure.persistence.entity.InventarioJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class InventarioJpaMapper {

    public Inventario toDomain(InventarioJpaEntity entity) {
        return new Inventario(entity.getId(),
                entity.getStockExtra(),
                entity.getStockAA(),
                entity.getStockA(),
                entity.getStockB());
    }

    public InventarioJpaEntity toJpa(Inventario inventario) {
        InventarioJpaEntity entity = new InventarioJpaEntity();
        entity.setId(inventario.getId());
        entity.setStockExtra(inventario.getStockExtra());
        entity.setStockAA(inventario.getStockAA());
        entity.setStockA(inventario.getStockA());
        entity.setStockB(inventario.getStockB());
        return entity;
    }
}
