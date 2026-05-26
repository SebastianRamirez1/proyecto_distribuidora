package com.distribuidora.huevos.application.dto.response;

import java.math.BigDecimal;

public class PrecioCostoResponse {

    private BigDecimal costoExtra;
    private BigDecimal costoAA;
    private BigDecimal costoA;
    private BigDecimal costoB;

    public PrecioCostoResponse() {}

    public PrecioCostoResponse(BigDecimal costoExtra, BigDecimal costoAA,
                               BigDecimal costoA, BigDecimal costoB) {
        this.costoExtra = costoExtra;
        this.costoAA    = costoAA;
        this.costoA     = costoA;
        this.costoB     = costoB;
    }

    public BigDecimal getCostoExtra() { return costoExtra; }
    public void setCostoExtra(BigDecimal costoExtra) { this.costoExtra = costoExtra; }

    public BigDecimal getCostoAA() { return costoAA; }
    public void setCostoAA(BigDecimal costoAA) { this.costoAA = costoAA; }

    public BigDecimal getCostoA() { return costoA; }
    public void setCostoA(BigDecimal costoA) { this.costoA = costoA; }

    public BigDecimal getCostoB() { return costoB; }
    public void setCostoB(BigDecimal costoB) { this.costoB = costoB; }
}
