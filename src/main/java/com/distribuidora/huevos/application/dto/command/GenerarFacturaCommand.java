package com.distribuidora.huevos.application.dto.command;

import com.distribuidora.huevos.domain.enums.TipoFactura;
import jakarta.validation.constraints.NotNull;

public class GenerarFacturaCommand {

    @NotNull(message = "El ID de la venta es obligatorio")
    private Long ventaId;

    private String nitCliente;   // opcional, si el cliente lo proporciona

    @NotNull(message = "El tipo de factura es obligatorio")
    private TipoFactura tipo = TipoFactura.MANUAL;

    public GenerarFacturaCommand() {}

    public Long getVentaId() { return ventaId; }
    public void setVentaId(Long ventaId) { this.ventaId = ventaId; }

    public String getNitCliente() { return nitCliente; }
    public void setNitCliente(String nitCliente) { this.nitCliente = nitCliente; }

    public TipoFactura getTipo() { return tipo; }
    public void setTipo(TipoFactura tipo) { this.tipo = tipo; }
}
