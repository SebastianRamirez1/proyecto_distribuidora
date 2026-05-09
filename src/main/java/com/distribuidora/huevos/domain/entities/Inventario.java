package com.distribuidora.huevos.domain.entities;

import com.distribuidora.huevos.domain.enums.TipoProducto;
import com.distribuidora.huevos.domain.exceptions.StockInsuficienteException;
import com.distribuidora.huevos.domain.valueobjects.Cantidad;

public class Inventario {

    private Long id;
    private int stockExtra;
    private int stockNormal;

    public Inventario(Long id, int stockExtra, int stockNormal) {
        this.id = id;
        this.stockExtra = stockExtra;
        this.stockNormal = stockNormal;
    }

    public void validarStock(TipoProducto tipo, Cantidad cantidad) {
        int disponible = tipo == TipoProducto.EXTRA ? stockExtra : stockNormal;
        if (disponible < cantidad.getValor()) {
            throw new StockInsuficienteException(
                    String.format("Stock insuficiente para canastas %s. Disponible: %d, solicitado: %d",
                            tipo.name(), disponible, cantidad.getValor()));
        }
    }

    public void descontar(TipoProducto tipo, Cantidad cantidad) {
        validarStock(tipo, cantidad);
        if (tipo == TipoProducto.EXTRA) {
            this.stockExtra -= cantidad.getValor();
        } else {
            this.stockNormal -= cantidad.getValor();
        }
    }

    public void agregar(TipoProducto tipo, Cantidad cantidad) {
        if (tipo == TipoProducto.EXTRA) {
            this.stockExtra += cantidad.getValor();
        } else {
            this.stockNormal += cantidad.getValor();
        }
    }

    public int obtenerStock(TipoProducto tipo) {
        return tipo == TipoProducto.EXTRA ? stockExtra : stockNormal;
    }

    public Long getId() {
        return id;
    }

    public int getStockExtra() {
        return stockExtra;
    }

    public int getStockNormal() {
        return stockNormal;
    }
}
