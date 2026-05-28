package com.distribuidora.huevos.domain.entities;

import com.distribuidora.huevos.domain.enums.TipoProducto;
import com.distribuidora.huevos.domain.exceptions.OperacionNoPermitidaException;
import com.distribuidora.huevos.domain.exceptions.StockInsuficienteException;
import com.distribuidora.huevos.domain.valueobjects.Cantidad;

public class Inventario {

    private Long id;
    /**
     * Stock de EXTRA en canastas. Puede tener precisión 0.5:
     *   80.0 = 80 canastas cerradas
     *   79.5 = 79 canastas cerradas + 1 canasta abierta (vendida la mitad)
     */
    private double stockExtra;
    /** Igual que stockExtra, con precisión 0.5 para medias de tipo AA. */
    private double stockAA;
    private int stockA;
    private int stockB;

    public Inventario(Long id, double stockExtra, double stockAA, int stockA, int stockB) {
        this.id = id;
        this.stockExtra = stockExtra;
        this.stockAA    = stockAA;
        this.stockA     = stockA;
        this.stockB     = stockB;
    }

    public double obtenerStock(TipoProducto tipo) {
        return switch (tipo) {
            case EXTRA, EXTRA_MEDIA -> stockExtra;
            case AA,    AA_MEDIA    -> stockAA;
            case A                  -> stockA;
            case B                  -> stockB;
        };
    }

    /**
     * Cuántas canastas se descuentan del stock para atender una venta.
     *
     *   - Canasta entera (EXTRA, AA, A, B):  1.0 por unidad
     *   - Media canasta (EXTRA_MEDIA, AA_MEDIA): 0.5 por unidad
     *
     * Con precisión 0.5, dos ventas separadas de 1 media consumen
     * exactamente 1 canasta entera entre las dos (0.5 + 0.5 = 1.0),
     * lo que refleja la realidad: se abre 1 canasta y se usan ambas mitades.
     */
    private double unidadesADescontar(TipoProducto tipo, Cantidad cantidad) {
        return switch (tipo) {
            case EXTRA_MEDIA, AA_MEDIA -> cantidad.getValor() * 0.5;
            default                    -> cantidad.getValor();
        };
    }

    public void validarStock(TipoProducto tipo, Cantidad cantidad) {
        double disponible = obtenerStock(tipo);
        double necesarias = unidadesADescontar(tipo, cantidad);
        if (disponible < necesarias) {
            throw new StockInsuficienteException(
                    String.format("Stock insuficiente para %s. Disponible: %.1f canastas, solicitado: %.1f",
                            tipo.name(), disponible, necesarias));
        }
    }

    public void descontar(TipoProducto tipo, Cantidad cantidad) {
        validarStock(tipo, cantidad);
        double delta = unidadesADescontar(tipo, cantidad);
        switch (tipo) {
            case EXTRA, EXTRA_MEDIA -> stockExtra -= delta;
            case AA,    AA_MEDIA    -> stockAA    -= delta;
            case A                  -> stockA     -= (int) delta;
            case B                  -> stockB     -= (int) delta;
        }
    }

    public void agregar(TipoProducto tipo, Cantidad cantidad) {
        // agregar siempre recibe canastas enteras (EXTRA_MEDIA/AA_MEDIA bloqueadas en servicio)
        double delta = unidadesADescontar(tipo, cantidad);
        switch (tipo) {
            case EXTRA, EXTRA_MEDIA -> stockExtra += delta;
            case AA,    AA_MEDIA    -> stockAA    += delta;
            case A                  -> stockA     += (int) delta;
            case B                  -> stockB     += (int) delta;
        }
    }

    /**
     * Establece el stock exacto para cada tipo.
     * Úsese para correcciones manuales (mermas, tomas sin venta registrada, conteo físico).
     */
    public void ajustar(double stockExtra, double stockAA, int stockA, int stockB) {
        if (stockExtra < 0 || stockAA < 0 || stockA < 0 || stockB < 0) {
            throw new OperacionNoPermitidaException("El stock no puede ser negativo");
        }
        this.stockExtra = stockExtra;
        this.stockAA    = stockAA;
        this.stockA     = stockA;
        this.stockB     = stockB;
    }

    public Long   getId()         { return id;         }
    public double getStockExtra() { return stockExtra; }
    public double getStockAA()    { return stockAA;    }
    public int    getStockA()     { return stockA;     }
    public int    getStockB()     { return stockB;     }
}
