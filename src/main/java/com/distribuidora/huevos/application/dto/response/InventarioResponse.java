package com.distribuidora.huevos.application.dto.response;

public class InventarioResponse {

    private int stockExtra;
    private int stockAA;
    private int stockA;
    private int stockB;

    public InventarioResponse() {}

    public InventarioResponse(int stockExtra, int stockAA, int stockA, int stockB) {
        this.stockExtra = stockExtra;
        this.stockAA = stockAA;
        this.stockA  = stockA;
        this.stockB  = stockB;
    }

    public int getStockExtra() { return stockExtra; }
    public void setStockExtra(int stockExtra) { this.stockExtra = stockExtra; }

    public int getStockAA() { return stockAA; }
    public void setStockAA(int stockAA) { this.stockAA = stockAA; }

    public int getStockA() { return stockA; }
    public void setStockA(int stockA) { this.stockA = stockA; }

    public int getStockB() { return stockB; }
    public void setStockB(int stockB) { this.stockB = stockB; }
}
