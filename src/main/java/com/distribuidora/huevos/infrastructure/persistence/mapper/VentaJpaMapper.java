package com.distribuidora.huevos.infrastructure.persistence.mapper;

import com.distribuidora.huevos.domain.entities.Cliente;
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
        // costoUnitario puede ser null en ventas anteriores a esta feature → Precio.cero()
        Precio costo = entity.getCostoUnitario() != null
                ? Precio.de(entity.getCostoUnitario())
                : Precio.cero();
        // cliente puede ser null para ventas al público general (sin cliente registrado)
        Cliente cliente = entity.getCliente() != null
                ? clienteJpaMapper.toDomain(entity.getCliente())
                : null;
        return new Venta(
                entity.getId(),
                cliente,
                entity.getTipoProducto(),
                new Cantidad(entity.getCantidad()),
                Precio.de(entity.getPrecioUnitario()),
                costo,
                entity.getTipoPago(),
                entity.getFecha(),
                entity.isAnulada(),
                entity.getFechaAnulacion());
    }

    public VentaJpaEntity toJpa(Venta venta) {
        VentaJpaEntity entity = new VentaJpaEntity();
        entity.setId(venta.getId());
        entity.setCliente(venta.getCliente() != null ? clienteJpaMapper.toJpa(venta.getCliente()) : null);
        entity.setTipoProducto(venta.getTipoProducto());
        entity.setCantidad(venta.getCantidad().getValor());
        entity.setPrecioUnitario(venta.getPrecioUnitario().getValor());
        entity.setCostoUnitario(venta.getCostoUnitario().getValor());
        entity.setTipoPago(venta.getTipoPago());
        entity.setFecha(venta.getFecha());
        entity.setAnulada(venta.isAnulada());
        entity.setFechaAnulacion(venta.getFechaAnulacion());
        return entity;
    }
}
