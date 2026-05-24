package com.distribuidora.huevos.application.dto.command;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class ActualizarPrecioPublicoCommand {

    @NotNull(message = "El precio EXTRA es obligatorio")
    @DecimalMin(value = "0", message = "El precio EXTRA no puede ser negativo")
    private BigDecimal precioExtra;

    @NotNull(message = "El precio AA es obligatorio")
    @DecimalMin(value = "0", message = "El precio AA no puede ser negativo")
    private BigDecimal precioAA;

    @NotNull(message = "El precio A es obligatorio")
    @DecimalMin(value = "0", message = "El precio A no puede ser negativo")
    private BigDecimal precioA;

    @NotNull(message = "El precio B es obligatorio")
    @DecimalMin(value = "0", message = "El precio B no puede ser negativo")
    private BigDecimal precioB;

    public ActualizarPrecioPublicoCommand() {}

    public BigDecimal getPrecioExtra() { return precioExtra; }
    public void setPrecioExtra(BigDecimal precioExtra) { this.precioExtra = precioExtra; }

    public BigDecimal getPrecioAA() { return precioAA; }
    public void setPrecioAA(BigDecimal precioAA) { this.precioAA = precioAA; }

    public BigDecimal getPrecioA() { return precioA; }
    public void setPrecioA(BigDecimal precioA) { this.precioA = precioA; }

    public BigDecimal getPrecioB() { return precioB; }
    public void setPrecioB(BigDecimal precioB) { this.precioB = precioB; }
}
