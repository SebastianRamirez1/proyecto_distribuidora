package com.distribuidora.huevos.application.dto.response;

import com.distribuidora.huevos.domain.enums.TipoCliente;

import java.math.BigDecimal;

public class ClienteResponse {

    private Long id;
    private String nombre;
    private TipoCliente tipo;

    // Precios especiales (para clientes ESPECIAL)
    private BigDecimal precioEspecialExtra;
    private BigDecimal precioEspecialAA;
    private BigDecimal precioEspecialA;
    private BigDecimal precioEspecialB;

    // Descuento por volumen (opcional)
    private Integer descuentoDesdeCanastas;
    private BigDecimal descuentoPrecioExtra;
    private BigDecimal descuentoPrecioAA;
    private BigDecimal descuentoPrecioA;
    private BigDecimal descuentoPrecioB;

    public ClienteResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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
