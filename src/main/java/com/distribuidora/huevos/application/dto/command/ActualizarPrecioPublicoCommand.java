package com.distribuidora.huevos.application.dto.command;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

// DECISIÓN: Precio público es un requisito implícito (clientes NORMAL necesitan precio).
// Se expone vía PUT /api/precios/publico para que el dueño lo actualice diariamente.
public class ActualizarPrecioPublicoCommand {

    @NotNull(message = "El precio EXTRA es obligatorio")
    @DecimalMin(value = "0", message = "El precio EXTRA no puede ser negativo")
    private BigDecimal precioExtra;

    @NotNull(message = "El precio NORMAL es obligatorio")
    @DecimalMin(value = "0", message = "El precio NORMAL no puede ser negativo")
    private BigDecimal precioNormal;

    public ActualizarPrecioPublicoCommand() {}

    public BigDecimal getPrecioExtra() { return precioExtra; }
    public void setPrecioExtra(BigDecimal precioExtra) { this.precioExtra = precioExtra; }

    public BigDecimal getPrecioNormal() { return precioNormal; }
    public void setPrecioNormal(BigDecimal precioNormal) { this.precioNormal = precioNormal; }
}
