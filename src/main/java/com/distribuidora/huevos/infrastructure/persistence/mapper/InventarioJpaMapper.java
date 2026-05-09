package com.distribuidora.huevos.infrastructure.persistence.mapper;

import com.distribuidora.huevos.domain.entities.Inventario;
import com.distribuidora.huevos.infrastructure.persistence.entity.InventarioJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class InventarioJpaMapper {

    public Inventario toDomain(InventarioJpaEntity entity) {
        return new Inventario(entity.getId(), entity.getStockExtra(), entity.getStockNormal());
    }

    public InventarioJpaEntity toJpa(Inventario inventario) {
        InventarioJpaEntity entity = new InventarioJpaEntity();
        entity.setId(inventario.getId());
        entity.setStockExtra(inventario.getStockExtra());
        entity.setStockNormal(inventario.getStockNormal());
        return entity;
    }
}
