package com.distribuidora.huevos.infrastructure.persistence.mapper;

import com.distribuidora.huevos.domain.entities.Caja;
import com.distribuidora.huevos.domain.valueobjects.Dinero;
import com.distribuidora.huevos.infrastructure.persistence.entity.CajaJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class CajaJpaMapper {

    public Caja toDomain(CajaJpaEntity entity) {
        return new Caja(
                entity.getId(),
                entity.getFecha(),
                Dinero.de(entity.getTotalEfectivo()),
                Dinero.de(entity.getTotalTransferencia()),
                Dinero.de(entity.getTotalFiado()),
                Dinero.de(entity.getTotalAbonos()));
    }

    public CajaJpaEntity toJpa(Caja caja) {
        CajaJpaEntity entity = new CajaJpaEntity();
        entity.setId(caja.getId());
        entity.setFecha(caja.getFecha());
        entity.setTotalEfectivo(caja.getTotalEfectivo().getValor());
        entity.setTotalTransferencia(caja.getTotalTransferencia().getValor());
        entity.setTotalFiado(caja.getTotalFiado().getValor());
        entity.setTotalAbonos(caja.getTotalAbonos().getValor());
        return entity;
    }
}
