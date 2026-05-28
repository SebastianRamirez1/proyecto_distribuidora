package com.distribuidora.huevos.application.dto.command;

import jakarta.validation.constraints.Min;

/**
 * Establece el stock exacto de cada tipo.
 * Usado para correcciones manuales: mermas, tomas sin registro, conteo físico, etc.
 */
public class AjustarInventarioCommand {

    @Min(value = 0, message = "El stock EXTRA no puede ser negativo")
    private int stockExtra;

    @Min(value = 0, message = "El stock AA no puede ser negativo")
    private int stockAA;

    @Min(value = 0, message = "El stock A no puede ser negativo")
    private int stockA;

    @Min(value = 0, message = "El stock B no puede ser negativo")
    private int stockB;

    public AjustarInventarioCommand() {}

    public int getStockExtra() { return stockExtra; }
    public void setStockExtra(int stockExtra) { this.stockExtra = stockExtra; }

    public int getStockAA() { return stockAA; }
    public void setStockAA(int stockAA) { this.stockAA = stockAA; }

    public int getStockA() { return stockA; }
    public void setStockA(int stockA) { this.stockA = stockA; }

    public int getStockB() { return stockB; }
    public void setStockB(int stockB) { this.stockB = stockB; }
}
