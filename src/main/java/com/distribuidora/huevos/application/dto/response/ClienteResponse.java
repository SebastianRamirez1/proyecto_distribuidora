package com.distribuidora.huevos.application.dto.response;

import com.distribuidora.huevos.domain.enums.TipoCliente;

import java.math.BigDecimal;

public class ClienteResponse {

    private Long id;
    private String nombre;
    private TipoCliente tipo;
    private BigDecimal precioEspecialExtra;
    private BigDecimal precioEspecialNormal;
    private Integer descuentoDesdeCanastas;
    private BigDecimal descuentoPrecioExtra;
    private BigDecimal descuentoPrecioNormal;

    public ClienteResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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
