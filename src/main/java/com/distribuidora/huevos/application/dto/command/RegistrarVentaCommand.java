package com.distribuidora.huevos.application.dto.command;

import com.distribuidora.huevos.domain.enums.TipoPago;
import com.distribuidora.huevos.domain.enums.TipoProducto;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class RegistrarVentaCommand {

    @NotNull(message = "El ID del cliente es obligatorio")
    private Long clienteId;

    @NotNull(message = "El tipo de producto es obligatorio")
    private TipoProducto tipoProducto;

    @Min(value = 1, message = "La cantidad debe ser mayor a 0")
    private int cantidad;

    @NotNull(message = "El tipo de pago es obligatorio")
    private TipoPago tipoPago;

    public RegistrarVentaCommand() {}

    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }

    public TipoProducto getTipoProducto() { return tipoProducto; }
    public void setTipoProducto(TipoProducto tipoProducto) { this.tipoProducto = tipoProducto; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public TipoPago getTipoPago() { return tipoPago; }
    public void setTipoPago(TipoPago tipoPago) { this.tipoPago = tipoPago; }
}
