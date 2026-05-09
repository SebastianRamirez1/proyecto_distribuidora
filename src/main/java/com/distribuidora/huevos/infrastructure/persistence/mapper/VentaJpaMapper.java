package com.distribuidora.huevos.infrastructure.persistence.mapper;

import com.distribuidora.huevos.domain.entities.Venta;
import com.distribuidora.huevos.domain.valueobjects.Cantidad;
import com.distribuidora.huevos.domain.valueobjects.Precio;
import com.distribuidora.huevos.infrastructure.persistence.entity.VentaJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class VentaJpaMapper {

    private final ClienteJpaMapper clienteJpaMapper;

    public VentaJpaMapper(ClienteJpaMapper clienteJpaMapper) {
        this.clienteJpaMapper = clienteJpaMapper;
    }

    public Venta toDomain(VentaJpaEntity entity) {
        return new Venta(
                entity.getId(),
                clienteJpaMapper.toDomain(entity.getCliente()),
                entity.getTipoProducto(),
                new Cantidad(entity.getCantidad()),
                Precio.de(entity.getPrecioUnitario()),
                entity.getTipoPago(),
                entity.getFecha());
    }

    public VentaJpaEntity toJpa(Venta venta) {
        VentaJpaEntity entity = new VentaJpaEntity();
        entity.setId(venta.getId());
        entity.setCliente(clienteJpaMapper.toJpa(venta.getCliente()));
        entity.setTipoProducto(venta.getTipoProducto());
        entity.setCantidad(venta.getCantidad().getValor());
        entity.setPrecioUnitario(venta.getPrecioUnitario().getValor());
        entity.setTipoPago(venta.getTipoPago());
        entity.setFecha(venta.getFecha());
        return entity;
    }
}
