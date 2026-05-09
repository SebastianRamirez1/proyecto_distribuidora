package com.distribuidora.huevos.domain.entities;

import com.distribuidora.huevos.domain.enums.TipoProducto;
import com.distribuidora.huevos.domain.exceptions.StockInsuficienteException;
import com.distribuidora.huevos.domain.valueobjects.Cantidad;

public class Inventario {

    private Long id;
    private int stockExtra;
    private int stockAA;
    private int stockA;
    private int stockB;

    public Inventario(Long id, int stockExtra, int stockAA, int stockA, int stockB) {
        this.id = id;
        this.stockExtra = stockExtra;
        this.stockAA = stockAA;
        this.stockA = stockA;
        this.stockB = stockB;
    }

    public int obtenerStock(TipoProducto tipo) {
        return switch (tipo) {
            case EXTRA -> stockExtra;
            case AA    -> stockAA;
            case A     -> stockA;
            case B     -> stockB;
        };
    }

    public void validarStock(TipoProducto tipo, Cantidad cantidad) {
        int disponible = obtenerStock(tipo);
        if (disponible < cantidad.getValor()) {
            throw new StockInsuficienteException(
                    String.format("Stock insuficiente para canastas %s. Disponible: %d, solicitado: %d",
                            tipo.name(), disponible, cantidad.getValor()));
        }
    }

    public void descontar(TipoProducto tipo, Cantidad cantidad) {
        validarStock(tipo, cantidad);
        switch (tipo) {
            case EXTRA -> stockExtra -= cantidad.getValor();
            case AA    -> stockAA    -= cantidad.getValor();
            case A     -> stockA     -= cantidad.getValor();
            case B     -> stockB     -= cantidad.getValor();
        }
    }

    public void agregar(TipoProducto tipo, Cantidad cantidad) {
        switch (tipo) {
            case EXTRA -> stockExtra += cantidad.getValor();
            case AA    -> stockAA    += cantidad.getValor();
            case A     -> stockA     += cantidad.getValor();
            case B     -> stockB     += cantidad.getValor();
        }
    }

    public Long getId()       { return id; }
    public int getStockExtra() { return stockExtra; }
    public int getStockAA()    { return stockAA; }
    public int getStockA()     { return stockA; }
    public int getStockB()     { return stockB; }
}
