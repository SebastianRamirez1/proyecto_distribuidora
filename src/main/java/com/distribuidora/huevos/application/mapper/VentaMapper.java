package com.distribuidora.huevos.application.mapper;

import com.distribuidora.huevos.application.dto.response.VentaResponse;
import com.distribuidora.huevos.domain.entities.Venta;
import org.springframework.stereotype.Component;

@Component
public class VentaMapper {

    public VentaResponse toResponse(Venta venta) {
        VentaResponse response = new VentaResponse();
        response.setId(venta.getId());
        response.setClienteId(venta.getCliente().getId());
        response.setNombreCliente(venta.getCliente().getNombre());
        response.setTipoProducto(venta.getTipoProducto());
        response.setCantidad(venta.getCantidad().getValor());
        response.setPrecioUnitario(venta.getPrecioUnitario().getValor());
        response.setTotal(venta.calcularTotal().getValor());
        response.setTipoPago(venta.getTipoPago());
        response.setFecha(venta.getFecha());
        return response;
    }
}
