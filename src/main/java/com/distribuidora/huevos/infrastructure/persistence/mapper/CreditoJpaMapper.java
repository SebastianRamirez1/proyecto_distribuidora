package com.distribuidora.huevos.infrastructure.persistence.mapper;

import com.distribuidora.huevos.domain.entities.Credito;
import com.distribuidora.huevos.domain.valueobjects.Dinero;
import com.distribuidora.huevos.infrastructure.persistence.entity.CreditoJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class CreditoJpaMapper {

    private final ClienteJpaMapper clienteJpaMapper;

    public CreditoJpaMapper(ClienteJpaMapper clienteJpaMapper) {
        this.clienteJpaMapper = clienteJpaMapper;
    }

    public Credito toDomain(CreditoJpaEntity entity) {
        return new Credito(
                entity.getId(),
                clienteJpaMapper.toDomain(entity.getCliente()),
                Dinero.de(entity.getMontoTotal()),
                Dinero.de(entity.getMontoPagado()));
    }

    public CreditoJpaEntity toJpa(Credito credito) {
        CreditoJpaEntity entity = new CreditoJpaEntity();
        entity.setId(credito.getId());
        entity.setCliente(clienteJpaMapper.toJpa(credito.getCliente()));
        entity.setMontoTotal(credito.getMontoTotal().getValor());
        entity.setMontoPagado(credito.getMontoPagado().getValor());
        return entity;
    }
}
