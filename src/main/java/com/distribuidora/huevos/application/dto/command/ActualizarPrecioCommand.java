package com.distribuidora.huevos.application.dto.command;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class ActualizarPrecioCommand {

    @NotNull(message = "El precio EXTRA es obligatorio")
    @DecimalMin(value = "0", message = "El precio EXTRA no puede ser negativo")
    private BigDecimal precioEspecialExtra;

    @NotNull(message = "El precio NORMAL es obligatorio")
    @DecimalMin(value = "0", message = "El precio NORMAL no puede ser negativo")
    private BigDecimal precioEspecialNormal;

    // Opcionales: si se envían los tres, se actualiza también el descuento por volumen
    private Integer descuentoDesdeCanastas;
    private BigDecimal descuentoPrecioExtra;
    private BigDecimal descuentoPrecioNormal;

    public ActualizarPrecioCommand() {}

    public BigDecimal getPrecioEspecialExtra() { return precioEspecialExtra; }
    public void setPrecioEspecialExtra(BigDecimal precioEspecialExtra) { this.precioEspecialExtra = precioEspecialExtra; }

    public BigDecimal getPrecioEspecialNormal() { return precioEspecialNormal; }
    public void setPrecioEspecialNormal(BigDecimal precioEspecialNormal) { this.precioEspecialNormal = precioEspecialNormal; }

    public Integer getDescuentoDesdeCanastas() { return descuentoDesdeCanastas; }
    public void setDescuentoDesdeCanastas(Integer descuentoDesdeCanastas) { this.descuentoDesdeCanastas = descuentoDesdeCanastas; }

    public BigDecimal getDescuentoPrecioExtra() { return descuentoPrecioExtra; }
    public void setDescuentoPrecioExtra(BigDecimal descuentoPrecioExtra) { this.descuentoPrecioExtra = descuentoPrecioExtra; }

    public BigDecimal getDescuentoPrecioNormal() { return descuentoPrecioNormal; }
    public void setDescuentoPrecioNormal(BigDecimal descuentoPrecioNormal) { this.descuentoPrecioNormal = descuentoPrecioNormal; }
}
