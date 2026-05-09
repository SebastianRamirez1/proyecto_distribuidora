package com.distribuidora.huevos.application.dto.response;

import java.math.BigDecimal;

public class PrecioPublicoResponse {
    private BigDecimal precioExtra;
    private BigDecimal precioNormal;

    public PrecioPublicoResponse() {}

    public PrecioPublicoResponse(BigDecimal precioExtra, BigDecimal precioNormal) {
        this.precioExtra = precioExtra;
        this.precioNormal = precioNormal;
    }

    public BigDecimal getPrecioExtra() { return precioExtra; }
    public void setPrecioExtra(BigDecimal precioExtra) { this.precioExtra = precioExtra; }

    public BigDecimal getPrecioNormal() { return precioNormal; }
    public void setPrecioNormal(BigDecimal precioNormal) { this.precioNormal = precioNormal; }
}
