package com.distribuidora.huevos.application.dto.command;

import com.distribuidora.huevos.domain.enums.TipoFactura;
import jakarta.validation.constraints.NotNull;

public class GenerarFacturaCommand {

    @NotNull(message = "El ID de la venta es obligatorio")
    private Long ventaId;

    private String nitCliente;       // opcional
    private String nombreCliente;   // opcional — sobreescribe el nombre del cliente registrado

    @NotNull(message = "El tipo de factura es obligatorio")
    private TipoFactura tipo = TipoFactura.MANUAL;

    public GenerarFacturaCommand() {}

    public Long getVentaId() { return ventaId; }
    public void setVentaId(Long ventaId) { this.ventaId = ventaId; }

    public String getNitCliente() { return nitCliente; }
    public void setNitCliente(String nitCliente) { this.nitCliente = nitCliente; }

    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }

    public TipoFactura getTipo() { return tipo; }
    public void setTipo(TipoFactura tipo) { this.tipo = tipo; }
}
