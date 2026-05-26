package com.distribuidora.huevos.application.dto.command;

import com.distribuidora.huevos.domain.enums.TipoCliente;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class ActualizarClienteCommand {

    @NotBlank(message = "El nombre del cliente es obligatorio")
    private String nombre;

    @NotNull(message = "El tipo de cliente es obligatorio")
    private TipoCliente tipo;

    // Requeridos si tipo == ESPECIAL
    private BigDecimal precioEspecialExtra;
    private BigDecimal precioEspecialAA;
    private BigDecimal precioEspecialA;
    private BigDecimal precioEspecialB;

    public ActualizarClienteCommand() {}

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public TipoCliente getTipo() { return tipo; }
    public void setTipo(TipoCliente tipo) { this.tipo = tipo; }

    public BigDecimal getPrecioEspecialExtra() { return precioEspecialExtra; }
    public void setPrecioEspecialExtra(BigDecimal v) { this.precioEspecialExtra = v; }

    public BigDecimal getPrecioEspecialAA() { return precioEspecialAA; }
    public void setPrecioEspecialAA(BigDecimal v) { this.precioEspecialAA = v; }

    public BigDecimal getPrecioEspecialA() { return precioEspecialA; }
    public void setPrecioEspecialA(BigDecimal v) { this.precioEspecialA = v; }

    public BigDecimal getPrecioEspecialB() { return precioEspecialB; }
    public void setPrecioEspecialB(BigDecimal v) { this.precioEspecialB = v; }
}
