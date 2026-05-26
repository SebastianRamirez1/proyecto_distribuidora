package com.distribuidora.huevos.application.dto.command;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class ActualizarPrecioCostoCommand {

    @NotNull(message = "El costo EXTRA es obligatorio")
    @DecimalMin(value = "0", message = "El costo EXTRA no puede ser negativo")
    private BigDecimal costoExtra;

    @NotNull(message = "El costo AA es obligatorio")
    @DecimalMin(value = "0", message = "El costo AA no puede ser negativo")
    private BigDecimal costoAA;

    @NotNull(message = "El costo A es obligatorio")
    @DecimalMin(value = "0", message = "El costo A no puede ser negativo")
    private BigDecimal costoA;

    @NotNull(message = "El costo B es obligatorio")
    @DecimalMin(value = "0", message = "El costo B no puede ser negativo")
    private BigDecimal costoB;

    public ActualizarPrecioCostoCommand() {}

    public BigDecimal getCostoExtra() { return costoExtra; }
    public void setCostoExtra(BigDecimal costoExtra) { this.costoExtra = costoExtra; }

    public BigDecimal getCostoAA() { return costoAA; }
    public void setCostoAA(BigDecimal costoAA) { this.costoAA = costoAA; }

    public BigDecimal getCostoA() { return costoA; }
    public void setCostoA(BigDecimal costoA) { this.costoA = costoA; }

    public BigDecimal getCostoB() { return costoB; }
    public void setCostoB(BigDecimal costoB) { this.costoB = costoB; }
}
