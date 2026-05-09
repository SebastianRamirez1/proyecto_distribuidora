package com.distribuidora.huevos.domain.entities;

import com.distribuidora.huevos.domain.enums.TipoProducto;
import com.distribuidora.huevos.domain.exceptions.StockInsuficienteException;
import com.distribuidora.huevos.domain.valueobjects.Cantidad;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class InventarioTest {

    @Test
    void descontarReduceElStockCorrectamente() {
        Inventario inventario = new Inventario(1L, 10, 20);
        inventario.descontar(TipoProducto.EXTRA, new Cantidad(3));
        assertThat(inventario.getStockExtra()).isEqualTo(7);
    }

    @Test
    void descontarStockNormalNoAfectaStockExtra() {
        Inventario inventario = new Inventario(1L, 10, 20);
        inventario.descontar(TipoProducto.NORMAL, new Cantidad(5));
        assertThat(inventario.getStockNormal()).isEqualTo(15);
        assertThat(inventario.getStockExtra()).isEqualTo(10);
    }

    @Test
    void descontarMasDelStockDisponibleLanzaExcepcion() {
        Inventario inventario = new Inventario(1L, 5, 10);
        assertThatThrownBy(() ->
                inventario.descontar(TipoProducto.EXTRA, new Cantidad(6)))
                .isInstanceOf(StockInsuficienteException.class)
                .hasMessageContaining("insuficiente");
    }

    @Test
    void descontarExactamenteElStockDisponibleFunciona() {
        Inventario inventario = new Inventario(1L, 5, 10);
        assertThatNoException().isThrownBy(() ->
                inventario.descontar(TipoProducto.EXTRA, new Cantidad(5)));
        assertThat(inventario.getStockExtra()).isEqualTo(0);
    }

    @Test
    void agregarAumentaElStockCorrectamente() {
        Inventario inventario = new Inventario(1L, 10, 20);
        inventario.agregar(TipoProducto.EXTRA, new Cantidad(5));
        assertThat(inventario.getStockExtra()).isEqualTo(15);
    }

    @Test
    void agregarStockNormalNoAfectaStockExtra() {
        Inventario inventario = new Inventario(1L, 10, 20);
        inventario.agregar(TipoProducto.NORMAL, new Cantidad(10));
        assertThat(inventario.getStockNormal()).isEqualTo(30);
        assertThat(inventario.getStockExtra()).isEqualTo(10);
    }

    @Test
    void stockInsuficienteConStockEnCeroLanzaExcepcion() {
        Inventario inventario = new Inventario(1L, 0, 0);
        assertThatThrownBy(() ->
                inventario.descontar(TipoProducto.EXTRA, new Cantidad(1)))
                .isInstanceOf(StockInsuficienteException.class)
                .hasMessageContaining("Disponible: 0");
    }
}
