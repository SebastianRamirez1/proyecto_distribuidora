package com.distribuidora.huevos.infrastructure.persistence.mapper;

import com.distribuidora.huevos.domain.entities.Cliente;
import com.distribuidora.huevos.domain.valueobjects.*;
import com.distribuidora.huevos.infrastructure.persistence.entity.ClienteJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class ClienteJpaMapper {

    public Cliente toDomain(ClienteJpaEntity entity) {
        PrecioEspecial precioEspecial = null;
        if (entity.getPrecioEspecialExtra() != null
                && entity.getPrecioEspecialAA() != null
                && entity.getPrecioEspecialA()  != null
                && entity.getPrecioEspecialB()  != null) {
            precioEspecial = new PrecioEspecial(
                    Precio.de(entity.getPrecioEspecialExtra()),
                    Precio.de(entity.getPrecioEspecialAA()),
                    Precio.de(entity.getPrecioEspecialA()),
                    Precio.de(entity.getPrecioEspecialB()));
        }

        DescuentoPorVolumen descuentoVolumen = null;
        if (entity.getDescuentoDesdeCanastas() != null
                && entity.getDescuentoPrecioExtra() != null
                && entity.getDescuentoPrecioAA()    != null
                && entity.getDescuentoPrecioA()     != null
                && entity.getDescuentoPrecioB()     != null) {
            descuentoVolumen = new DescuentoPorVolumen(
                    new Cantidad(entity.getDescuentoDesdeCanastas()),
                    Precio.de(entity.getDescuentoPrecioExtra()),
                    Precio.de(entity.getDescuentoPrecioAA()),
                    Precio.de(entity.getDescuentoPrecioA()),
                    Precio.de(entity.getDescuentoPrecioB()));
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
            entity.setPrecioEspecialAA(cliente.getPrecioEspecial().getPrecioAA().getValor());
            entity.setPrecioEspecialA(cliente.getPrecioEspecial().getPrecioA().getValor());
            entity.setPrecioEspecialB(cliente.getPrecioEspecial().getPrecioB().getValor());
        }

        if (cliente.getDescuentoVolumen() != null) {
            entity.setDescuentoDesdeCanastas(cliente.getDescuentoVolumen().getDesdeCanastas().getValor());
            entity.setDescuentoPrecioExtra(cliente.getDescuentoVolumen().getPrecioExtra().getValor());
            entity.setDescuentoPrecioAA(cliente.getDescuentoVolumen().getPrecioAA().getValor());
            entity.setDescuentoPrecioA(cliente.getDescuentoVolumen().getPrecioA().getValor());
            entity.setDescuentoPrecioB(cliente.getDescuentoVolumen().getPrecioB().getValor());
        }

        return entity;
    }
}
