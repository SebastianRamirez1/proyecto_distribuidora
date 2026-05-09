package com.distribuidora.huevos.infrastructure.persistence.mapper;

import com.distribuidora.huevos.domain.entities.Cliente;
import com.distribuidora.huevos.domain.valueobjects.*;
import com.distribuidora.huevos.infrastructure.persistence.entity.ClienteJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class ClienteJpaMapper {

    public Cliente toDomain(ClienteJpaEntity entity) {
        PrecioEspecial precioEspecial = null;
        if (entity.getPrecioEspecialExtra() != null && entity.getPrecioEspecialNormal() != null) {
            precioEspecial = new PrecioEspecial(
                    Precio.de(entity.getPrecioEspecialExtra()),
                    Precio.de(entity.getPrecioEspecialNormal()));
        }

        DescuentoPorVolumen descuentoVolumen = null;
        if (entity.getDescuentoDesdeCanastas() != null
                && entity.getDescuentoPrecioExtra() != null
                && entity.getDescuentoPrecioNormal() != null) {
            descuentoVolumen = new DescuentoPorVolumen(
                    new Cantidad(entity.getDescuentoDesdeCanastas()),
                    Precio.de(entity.getDescuentoPrecioExtra()),
                    Precio.de(entity.getDescuentoPrecioNormal()));
        }

        return new Cliente(entity.getId(), entity.getNombre(), entity.getTipo(),
                precioEspecial, descuentoVolumen);
    }

    public ClienteJpaEntity toJpa(Cliente cliente) {
        ClienteJpaEntity entity = new ClienteJpaEntity();
        entity.setId(cliente.getId());
        entity.setNombre(cliente.getNombre());
        entity.setTipo(cliente.getTipo());

        if (cliente.getPrecioEspecial() != null) {
            entity.setPrecioEspecialExtra(cliente.getPrecioEspecial().getPrecioExtra().getValor());
            entity.setPrecioEspecialNormal(cliente.getPrecioEspecial().getPrecioNormal().getValor());
        }

        if (cliente.getDescuentoVolumen() != null) {
            entity.setDescuentoDesdeCanastas(cliente.getDescuentoVolumen().getDesdeCanastas().getValor());
            entity.setDescuentoPrecioExtra(cliente.getDescuentoVolumen().getPrecioExtra().getValor());
            entity.setDescuentoPrecioNormal(cliente.getDescuentoVolumen().getPrecioNormal().getValor());
        }

        return entity;
    }
}
