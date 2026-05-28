package com.distribuidora.huevos.infrastructure.persistence.mapper;

import com.distribuidora.huevos.domain.entities.Inventario;
import com.distribuidora.huevos.infrastructure.persistence.entity.InventarioJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class InventarioJpaMapper {

    public Inventario toDomain(InventarioJpaEntity entity) {
        return new Inventario(entity.getId(),
                entity.getStockExtra() != null ? entity.getStockExtra() : 0.0,
                entity.getStockAA()    != null ? entity.getStockAA()    : 0.0,
                entity.getStockA()     != null ? entity.getStockA()     : 0,
                entity.getStockB()     != null ? entity.getStockB()     : 0);
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
