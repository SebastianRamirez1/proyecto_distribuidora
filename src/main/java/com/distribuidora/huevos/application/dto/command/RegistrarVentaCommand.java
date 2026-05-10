package com.distribuidora.huevos.application.dto.command;

import com.distribuidora.huevos.domain.enums.TipoPago;
import com.distribuidora.huevos.domain.enums.TipoProducto;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class RegistrarVentaCommand {

    @NotNull(message = "El ID del cliente es obligatorio")
    private Long clienteId;

    @NotNull(message = "El tipo de producto es obligatorio")
    private TipoProducto tipoProducto;

    @Min(value = 1, message = "La cantidad debe ser mayor a 0")
    private int cantidad;

    @NotNull(message = "El tipo de pago es obligatorio")
    private TipoPago tipoPago;

    /** Opcional. Si se envía, sobreescribe el precio calculado (rebaja puntual). */
    @DecimalMin(value = "0.01", message = "El precio manual debe ser mayor a 0")
    private BigDecimal precioManual;

    public RegistrarVentaCommand() {}

    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }

    public TipoProducto getTipoProducto() { return tipoProducto; }
    public void setTipoProducto(TipoProducto tipoProducto) { this.tipoProducto = tipoProducto; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public TipoPago getTipoPago() { return tipoPago; }
    public void setTipoPago(TipoPago tipoPago) { this.tipoPago = tipoPago; }

    public BigDecimal getPrecioManual() { return precioManual; }
    public void setPrecioManual(BigDecimal precioManual) { this.precioManual = precioManual; }
}
