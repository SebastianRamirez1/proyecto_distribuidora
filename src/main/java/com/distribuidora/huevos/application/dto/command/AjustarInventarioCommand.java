package com.distribuidora.huevos.application.dto.command;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;

/**
 * Establece el stock exacto de cada tipo.
 * stockExtra y stockAA aceptan 0.5 para representar media canasta abierta.
 */
public class AjustarInventarioCommand {

    @DecimalMin(value = "0.0", message = "El stock EXTRA no puede ser negativo")
    private double stockExtra;

    @DecimalMin(value = "0.0", message = "El stock AA no puede ser negativo")
    private double stockAA;

    @Min(value = 0, message = "El stock A no puede ser negativo")
    private int stockA;

    @Min(value = 0, message = "El stock B no puede ser negativo")
    private int stockB;

    public AjustarInventarioCommand() {}

    public double getStockExtra() { return stockExtra; }
    public void   setStockExtra(double stockExtra) { this.stockExtra = stockExtra; }

    public double getStockAA() { return stockAA; }
    public void   setStockAA(double stockAA) { this.stockAA = stockAA; }

    public int  getStockA() { return stockA; }
    public void setStockA(int stockA) { this.stockA = stockA; }

    public int  getStockB() { return stockB; }
    public void setStockB(int stockB) { this.stockB = stockB; }
}
