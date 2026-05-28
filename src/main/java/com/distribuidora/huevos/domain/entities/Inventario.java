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
            case EXTRA, EXTRA_MEDIA -> stockExtra;
            case AA,    AA_MEDIA    -> stockAA;
            case A                  -> stockA;
            case B                  -> stockB;
        };
    }

    /**
     * Cuántas canastas ENTERAS se necesitan para atender una venta del tipo dado.
     * Para medias canastas: ceil(n/2) — p.ej. 1 media = 1 entera abierta; 2 medias = 1 entera.
     */
    private int canastasEnteras(TipoProducto tipo, Cantidad cantidad) {
        return switch (tipo) {
            case EXTRA_MEDIA, AA_MEDIA -> (cantidad.getValor() + 1) / 2;
            default                    -> cantidad.getValor();
        };
    }

    public void validarStock(TipoProducto tipo, Cantidad cantidad) {
        int disponible  = obtenerStock(tipo);
        int necesarias  = canastasEnteras(tipo, cantidad);
        if (disponible < necesarias) {
            throw new StockInsuficienteException(
                    String.format("Stock insuficiente para %s. Disponible: %d canastas, solicitado: %d",
                            tipo.name(), disponible, necesarias));
        }
    }

    public void descontar(TipoProducto tipo, Cantidad cantidad) {
        validarStock(tipo, cantidad);
        int canastas = canastasEnteras(tipo, cantidad);
        switch (tipo) {
            case EXTRA, EXTRA_MEDIA -> stockExtra -= canastas;
            case AA,    AA_MEDIA    -> stockAA    -= canastas;
            case A                  -> stockA     -= canastas;
            case B                  -> stockB     -= canastas;
        }
    }

    public void agregar(TipoProducto tipo, Cantidad cantidad) {
        int canastas = canastasEnteras(tipo, cantidad);
        switch (tipo) {
            case EXTRA, EXTRA_MEDIA -> stockExtra += canastas;
            case AA,    AA_MEDIA    -> stockAA    += canastas;
            case A                  -> stockA     += canastas;
            case B                  -> stockB     += canastas;
        }
    }

    public Long getId()       { return id; }
    public int getStockExtra() { return stockExtra; }
    public int getStockAA()    { return stockAA; }
    public int getStockA()     { return stockA; }
    public int getStockB()     { return stockB; }
}
