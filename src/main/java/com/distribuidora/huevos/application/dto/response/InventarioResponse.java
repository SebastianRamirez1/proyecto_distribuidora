package com.distribuidora.huevos.application.dto.response;

public class InventarioResponse {

    private int stockExtra;
    private int stockNormal;

    public InventarioResponse() {}

    public InventarioResponse(int stockExtra, int stockNormal) {
        this.stockExtra = stockExtra;
        this.stockNormal = stockNormal;
    }

    public int getStockExtra() { return stockExtra; }
    public void setStockExtra(int stockExtra) { this.stockExtra = stockExtra; }

    public int getStockNormal() { return stockNormal; }
    public void setStockNormal(int stockNormal) { this.stockNormal = stockNormal; }
}
