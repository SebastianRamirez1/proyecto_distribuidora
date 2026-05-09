package com.distribuidora.huevos.application.dto.command;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class ActualizarPrecioCommand {

    @NotNull(message = "El precio especial EXTRA es obligatorio")
    @DecimalMin(value = "0", message = "El precio especial EXTRA no puede ser negativo")
    private BigDecimal precioEspecialExtra;

    @NotNull(message = "El precio especial AA es obligatorio")
    @DecimalMin(value = "0", message = "El precio especial AA no puede ser negativo")
    private BigDecimal precioEspecialAA;

    @NotNull(message = "El precio especial A es obligatorio")
    @DecimalMin(value = "0", message = "El precio especial A no puede ser negativo")
    private BigDecimal precioEspecialA;

    @NotNull(message = "El precio especial B es obligatorio")
    @DecimalMin(value = "0", message = "El precio especial B no puede ser negativo")
    private BigDecimal precioEspecialB;

    // Opcionales: si se envían los cinco, se actualiza también el descuento por volumen
    private Integer descuentoDesdeCanastas;
    private BigDecimal descuentoPrecioExtra;
    private BigDecimal descuentoPrecioAA;
    private BigDecimal descuentoPrecioA;
    private BigDecimal descuentoPrecioB;

    public ActualizarPrecioCommand() {}

    public BigDecimal getPrecioEspecialExtra() { return precioEspecialExtra; }
    public void setPrecioEspecialExtra(BigDecimal v) { this.precioEspecialExtra = v; }

    public BigDecimal getPrecioEspecialAA() { return precioEspecialAA; }
    public void setPrecioEspecialAA(BigDecimal v) { this.precioEspecialAA = v; }

    public BigDecimal getPrecioEspecialA() { return precioEspecialA; }
    public void setPrecioEspecialA(BigDecimal v) { this.precioEspecialA = v; }

    public BigDecimal getPrecioEspecialB() { return precioEspecialB; }
    public void setPrecioEspecialB(BigDecimal v) { this.precioEspecialB = v; }

    public Integer getDescuentoDesdeCanastas() { return descuentoDesdeCanastas; }
    public void setDescuentoDesdeCanastas(Integer v) { this.descuentoDesdeCanastas = v; }

    public BigDecimal getDescuentoPrecioExtra() { return descuentoPrecioExtra; }
    public void setDescuentoPrecioExtra(BigDecimal v) { this.descuentoPrecioExtra = v; }

    public BigDecimal getDescuentoPrecioAA() { return descuentoPrecioAA; }
    public void setDescuentoPrecioAA(BigDecimal v) { this.descuentoPrecioAA = v; }

    public BigDecimal getDescuentoPrecioA() { return descuentoPrecioA; }
    public void setDescuentoPrecioA(BigDecimal v) { this.descuentoPrecioA = v; }

    public BigDecimal getDescuentoPrecioB() { return descuentoPrecioB; }
    public void setDescuentoPrecioB(BigDecimal v) { this.descuentoPrecioB = v; }
}
