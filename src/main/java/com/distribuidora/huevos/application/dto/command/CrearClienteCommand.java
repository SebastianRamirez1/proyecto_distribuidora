package com.distribuidora.huevos.application.dto.command;

import com.distribuidora.huevos.domain.enums.TipoCliente;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class CrearClienteCommand {

    @NotBlank(message = "El nombre del cliente es obligatorio")
    private String nombre;

    @NotNull(message = "El tipo de cliente es obligatorio")
    private TipoCliente tipo;

    // Requeridos si tipo == ESPECIAL
    private BigDecimal precioEspecialExtra;
    private BigDecimal precioEspecialNormal;

    // Opcionales (descuento por volumen para clientes ESPECIAL)
    private Integer descuentoDesdeCanastas;
    private BigDecimal descuentoPrecioExtra;
    private BigDecimal descuentoPrecioNormal;

    public CrearClienteCommand() {}

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public TipoCliente getTipo() { return tipo; }
    public void setTipo(TipoCliente tipo) { this.tipo = tipo; }

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
