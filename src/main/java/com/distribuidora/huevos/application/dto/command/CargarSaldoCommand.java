package com.distribuidora.huevos.application.dto.command;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class CargarSaldoCommand {

    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor a cero")
    private BigDecimal monto;

    /** Descripción libre: "Deuda cuaderno al 26/05/2025", etc. Opcional. */
    private String descripcion;

    public CargarSaldoCommand() {}

    public BigDecimal getMonto()            { return monto; }
    public void setMonto(BigDecimal monto)  { this.monto = monto; }

    public String getDescripcion()                  { return descripcion; }
    public void setDescripcion(String descripcion)  { this.descripcion = descripcion; }
}
