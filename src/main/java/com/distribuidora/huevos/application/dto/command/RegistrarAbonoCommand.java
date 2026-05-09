package com.distribuidora.huevos.application.dto.command;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class RegistrarAbonoCommand {

    @NotNull(message = "El ID del cliente es obligatorio")
    private Long clienteId;

    @NotNull(message = "El monto del abono es obligatorio")
    @DecimalMin(value = "0.01", message = "El abono debe ser mayor a 0")
    private BigDecimal monto;

    public RegistrarAbonoCommand() {}

    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }

    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }
}
