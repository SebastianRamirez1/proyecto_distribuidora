package com.distribuidora.huevos.application.dto.response;

public class InventarioResponse {

    /** En canastas. Puede ser .5 cuando hay una canasta abierta (media vendida). */
    private double stockExtra;
    private double stockAA;
    private int    stockA;
    private int    stockB;

    public InventarioResponse() {}

    public InventarioResponse(double stockExtra, double stockAA, int stockA, int stockB) {
        this.stockExtra = stockExtra;
        this.stockAA    = stockAA;
        this.stockA     = stockA;
        this.stockB     = stockB;
    }

    public double getStockExtra() { return stockExtra; }
    public void   setStockExtra(double stockExtra) { this.stockExtra = stockExtra; }

    public double getStockAA() { return stockAA; }
    public void   setStockAA(double stockAA) { this.stockAA = stockAA; }

    public int  getStockA() { return stockA; }
    public void setStockA(int stockA) { this.stockA = stockA; }

    public int  getStockB() { return stockB; }
    public void setStockB(int stockB) { this.stockB = stockB; }
}
