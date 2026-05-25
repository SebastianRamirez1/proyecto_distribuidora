package com.distribuidora.huevos.application.mapper;

import com.distribuidora.huevos.application.dto.response.VentaResponse;
import com.distribuidora.huevos.domain.entities.Venta;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

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
        response.setCostoUnitario(venta.getCostoUnitario().getValor());
        // Solo mostramos ganancia si el costo fue configurado (> 0)
        boolean costoConfigurado = venta.getCostoUnitario().getValor()
                .compareTo(BigDecimal.ZERO) > 0;
        response.setGanancia(costoConfigurado ? venta.calcularGanancia().getValor() : null);
        response.setTotal(venta.calcularTotal().getValor());
        response.setTipoPago(venta.getTipoPago());
        response.setFecha(venta.getFecha());
        response.setAnulada(venta.isAnulada());
        return response;
    }
}
