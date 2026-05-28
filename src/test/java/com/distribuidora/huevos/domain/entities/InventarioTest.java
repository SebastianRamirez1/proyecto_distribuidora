package com.distribuidora.huevos.domain.entities;

import com.distribuidora.huevos.domain.enums.TipoProducto;
import com.distribuidora.huevos.domain.exceptions.StockInsuficienteException;
import com.distribuidora.huevos.domain.valueobjects.Cantidad;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class InventarioTest {

    @Test
    void descontarReduceElStockCorrectamente() {
        Inventario inventario = new Inventario(1L, 10, 0, 0, 0);
        inventario.descontar(TipoProducto.EXTRA, new Cantidad(3));
        assertThat(inventario.getStockExtra()).isEqualTo(7.0);
    }

    @Test
    void descontarStockANoAfectaStockExtra() {
        Inventario inventario = new Inventario(1L, 10, 0, 20, 0);
        inventario.descontar(TipoProducto.A, new Cantidad(5));
        assertThat(inventario.getStockA()).isEqualTo(15);
        assertThat(inventario.getStockExtra()).isEqualTo(10.0);
    }

    @Test
    void descontarMasDelStockDisponibleLanzaExcepcion() {
        Inventario inventario = new Inventario(1L, 5, 0, 0, 0);
        assertThatThrownBy(() ->
                inventario.descontar(TipoProducto.EXTRA, new Cantidad(6)))
                .isInstanceOf(StockInsuficienteException.class)
                .hasMessageContaining("insuficiente");
    }

    @Test
    void descontarExactamenteElStockDisponibleFunciona() {
        Inventario inventario = new Inventario(1L, 5, 0, 0, 0);
        assertThatNoException().isThrownBy(() ->
                inventario.descontar(TipoProducto.EXTRA, new Cantidad(5)));
        assertThat(inventario.getStockExtra()).isEqualTo(0.0);
    }

    @Test
    void agregarAumentaElStockCorrectamente() {
        Inventario inventario = new Inventario(1L, 10, 0, 0, 0);
        inventario.agregar(TipoProducto.EXTRA, new Cantidad(5));
        assertThat(inventario.getStockExtra()).isEqualTo(15.0);
    }

    @Test
    void agregarStockAANoAfectaStockExtra() {
        Inventario inventario = new Inventario(1L, 10, 20, 0, 0);
        inventario.agregar(TipoProducto.AA, new Cantidad(10));
        assertThat(inventario.getStockAA()).isEqualTo(30.0);
        assertThat(inventario.getStockExtra()).isEqualTo(10.0);
    }

    @Test
    void stockInsuficienteConStockEnCeroLanzaExcepcion() {
        Inventario inventario = new Inventario(1L, 0, 0, 0, 0);
        assertThatThrownBy(() ->
                inventario.descontar(TipoProducto.EXTRA, new Cantidad(1)))
                .isInstanceOf(StockInsuficienteException.class)
                .hasMessageContaining("Disponible: 0");
    }

    // ── Media canasta (EXTRA_MEDIA / AA_MEDIA) ────────────────────────────────

    @Test
    void descontarMediaExtraDescuenta0punto5PorUnidad() {
        // Cada unidad vendida de EXTRA_MEDIA consume 0.5 canastas del stock de EXTRA
        Inventario inventario = new Inventario(1L, 10, 0, 0, 0);
        inventario.descontar(TipoProducto.EXTRA_MEDIA, new Cantidad(1));
        assertThat(inventario.getStockExtra()).isEqualTo(9.5);
    }

    @Test
    void descontarMediaAADescuenta0punto5PorUnidad() {
        Inventario inventario = new Inventario(1L, 0, 10, 0, 0);
        inventario.descontar(TipoProducto.AA_MEDIA, new Cantidad(1));
        assertThat(inventario.getStockAA()).isEqualTo(9.5);
    }

    @Test
    void dosVentasDeMediaCanastaEquivaleAUnaEntera() {
        // 2 ventas separadas de 1 media = 1.0 canasta consumida en total
        Inventario inventario = new Inventario(1L, 5, 0, 0, 0);
        inventario.descontar(TipoProducto.EXTRA_MEDIA, new Cantidad(1));
        inventario.descontar(TipoProducto.EXTRA_MEDIA, new Cantidad(1));
        assertThat(inventario.getStockExtra()).isEqualTo(4.0);
    }

    @Test
    void ventaMediaCuandoStockEsMediaExactaFunciona() {
        // Stock = 0.5 (exactamente lo que queda de una canasta abierta): debe venderse sin error
        Inventario inventario = new Inventario(1L, 0.5, 0, 0, 0);
        assertThatNoException().isThrownBy(() ->
                inventario.descontar(TipoProducto.EXTRA_MEDIA, new Cantidad(1)));
        assertThat(inventario.getStockExtra()).isEqualTo(0.0);
    }

    @Test
    void ventaMediaCuandoStockCeroLanzaExcepcion() {
        Inventario inventario = new Inventario(1L, 0, 0, 0, 0);
        assertThatThrownBy(() ->
                inventario.descontar(TipoProducto.EXTRA_MEDIA, new Cantidad(1)))
                .isInstanceOf(StockInsuficienteException.class)
                .hasMessageContaining("insuficiente");
    }

    @Test
    void mediaExtraNoAfectaStockAA() {
        // Vender 2 medias EXTRA descuenta 1.0 de EXTRA pero deja AA intacto
        Inventario inventario = new Inventario(1L, 10, 10, 0, 0);
        inventario.descontar(TipoProducto.EXTRA_MEDIA, new Cantidad(2));
        assertThat(inventario.getStockExtra()).isEqualTo(9.0); // 10 - (2 × 0.5)
        assertThat(inventario.getStockAA()).isEqualTo(10.0);
    }

    @Test
    void mediaAANoAfectaStockExtra() {
        // Vender 2 medias AA descuenta 1.0 de AA pero deja EXTRA intacto
        Inventario inventario = new Inventario(1L, 10, 10, 0, 0);
        inventario.descontar(TipoProducto.AA_MEDIA, new Cantidad(2));
        assertThat(inventario.getStockAA()).isEqualTo(9.0); // 10 - (2 × 0.5)
        assertThat(inventario.getStockExtra()).isEqualTo(10.0);
    }

    @Test
    void agregarMediaCanastaAumenta0punto5PorUnidad() {
        // Este flujo ocurre al anular una venta de tipo EXTRA_MEDIA
        Inventario inventario = new Inventario(1L, 9.5, 0, 0, 0);
        inventario.agregar(TipoProducto.EXTRA_MEDIA, new Cantidad(1));
        assertThat(inventario.getStockExtra()).isEqualTo(10.0);
    }
}
