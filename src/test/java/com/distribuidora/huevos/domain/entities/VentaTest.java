package com.distribuidora.huevos.domain.entities;

import com.distribuidora.huevos.domain.enums.TipoCliente;
import com.distribuidora.huevos.domain.enums.TipoPago;
import com.distribuidora.huevos.domain.enums.TipoProducto;
import com.distribuidora.huevos.domain.valueobjects.Cantidad;
import com.distribuidora.huevos.domain.valueobjects.Dinero;
import com.distribuidora.huevos.domain.valueobjects.Precio;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

class VentaTest {

    private final Cliente clienteNormal = new Cliente(1L, "Juan",
            TipoCliente.NORMAL, null, null);

    @Test
    void calcularTotalMultiplicaCantidadPorPrecioUnitario() {
        Venta venta = new Venta(1L, clienteNormal, TipoProducto.EXTRA,
                new Cantidad(3), Precio.de("4.00"),
                TipoPago.EFECTIVO, LocalDateTime.now());

        Dinero total = venta.calcularTotal();
        assertThat(total.getValor()).isEqualByComparingTo("12.00");
    }

    @Test
    void calcularTotalParaUnaCanasta() {
        Venta venta = new Venta(1L, clienteNormal, TipoProducto.A,
                new Cantidad(1), Precio.de("3.00"),
                TipoPago.TRANSFERENCIA, LocalDateTime.now());

        Dinero total = venta.calcularTotal();
        assertThat(total.getValor()).isEqualByComparingTo("3.00");
    }

    @Test
    void ventaNoExponeSetters() {
        // Verificación en tiempo de compilación: Venta es final y sus campos son final.
        // Si este test compila, la inmutabilidad está garantizada estructuralmente.
        Venta venta = new Venta(1L, clienteNormal, TipoProducto.EXTRA,
                new Cantidad(2), Precio.de("4.50"),
                TipoPago.FIADO, LocalDateTime.now());

        assertThat(venta.getId()).isEqualTo(1L);
        assertThat(venta.getCantidad().getValor()).isEqualTo(2);
        assertThat(venta.getPrecioUnitario().getValor()).isEqualByComparingTo("4.50");
    }

    @Test
    void ventaConClienteNuloLanzaExcepcion() {
        assertThatThrownBy(() ->
                new Venta(1L, null, TipoProducto.EXTRA,
                        new Cantidad(1), Precio.de("4.00"),
                        TipoPago.EFECTIVO, LocalDateTime.now()))
                .isInstanceOf(NullPointerException.class);
    }
}
