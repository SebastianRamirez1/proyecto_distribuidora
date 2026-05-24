package com.distribuidora.huevos.application.dto.response;

import java.math.BigDecimal;

public class PrecioPublicoResponse {

    private BigDecimal precioExtra;
    private BigDecimal precioAA;
    private BigDecimal precioA;
    private BigDecimal precioB;

    public PrecioPublicoResponse() {}

    public PrecioPublicoResponse(BigDecimal precioExtra, BigDecimal precioAA,
                                 BigDecimal precioA, BigDecimal precioB) {
        this.precioExtra = precioExtra;
        this.precioAA = precioAA;
        this.precioA  = precioA;
        this.precioB  = precioB;
    }

    public BigDecimal getPrecioExtra() { return precioExtra; }
    public void setPrecioExtra(BigDecimal precioExtra) { this.precioExtra = precioExtra; }

    public BigDecimal getPrecioAA() { return precioAA; }
    public void setPrecioAA(BigDecimal precioAA) { this.precioAA = precioAA; }

    public BigDecimal getPrecioA() { return precioA; }
    public void setPrecioA(BigDecimal precioA) { this.precioA = precioA; }

    public BigDecimal getPrecioB() { return precioB; }
    public void setPrecioB(BigDecimal precioB) { this.precioB = precioB; }
}
