package com.distribuidora.huevos.domain.entities;

import com.distribuidora.huevos.domain.enums.TipoCliente;
import com.distribuidora.huevos.domain.enums.TipoProducto;
import com.distribuidora.huevos.domain.exceptions.ClienteIncompletoException;
import com.distribuidora.huevos.domain.valueobjects.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

class ClienteTest {

    private final PrecioPublico precioPublico = new PrecioPublico(1L,
            Precio.de("4.00"), Precio.de("3.00"));

    @Test
    void clienteNormalPagaPrecioPublico() {
        Cliente cliente = new Cliente(1L, "Juan", TipoCliente.NORMAL, null, null);
        Precio precio = cliente.calcularPrecio(TipoProducto.EXTRA, new Cantidad(1), precioPublico);
        assertThat(precio.getValor()).isEqualByComparingTo("4.00");
    }

    @Test
    void clienteNormalPagaPrecioPublicoParaHuevoNormal() {
        Cliente cliente = new Cliente(1L, "Juan", TipoCliente.NORMAL, null, null);
        Precio precio = cliente.calcularPrecio(TipoProducto.NORMAL, new Cantidad(1), precioPublico);
        assertThat(precio.getValor()).isEqualByComparingTo("3.00");
    }

    @Test
    void clienteEspecialPagaSuPrecioEspecial() {
        PrecioEspecial precioEspecial = new PrecioEspecial(
                Precio.de("3.50"), Precio.de("2.80"));
        Cliente cliente = new Cliente(1L, "Bodega Lopez", TipoCliente.ESPECIAL,
                precioEspecial, null);

        Precio precio = cliente.calcularPrecio(TipoProducto.EXTRA, new Cantidad(2), precioPublico);
        assertThat(precio.getValor()).isEqualByComparingTo("3.50");
    }

    @Test
    void clienteEspecialConDescuentoVolumenCalculaPrecioCorrectamente() {
        PrecioEspecial precioEspecial = new PrecioEspecial(
                Precio.de("3.50"), Precio.de("2.80"));
        DescuentoPorVolumen descuento = new DescuentoPorVolumen(
                new Cantidad(5),
                Precio.de("3.20"),
                Precio.de("2.50"));
        Cliente cliente = new Cliente(1L, "Mayorista Perez", TipoCliente.ESPECIAL,
                precioEspecial, descuento);

        Precio precioConDescuento = cliente.calcularPrecio(
                TipoProducto.EXTRA, new Cantidad(5), precioPublico);
        assertThat(precioConDescuento.getValor()).isEqualByComparingTo("3.20");
    }

    @Test
    void clienteEspecialSinAlcanzarUmbralNoAplicaDescuento() {
        PrecioEspecial precioEspecial = new PrecioEspecial(
                Precio.de("3.50"), Precio.de("2.80"));
        DescuentoPorVolumen descuento = new DescuentoPorVolumen(
                new Cantidad(5),
                Precio.de("3.20"),
                Precio.de("2.50"));
        Cliente cliente = new Cliente(1L, "Mayorista Perez", TipoCliente.ESPECIAL,
                precioEspecial, descuento);

        Precio precioSinDescuento = cliente.calcularPrecio(
                TipoProducto.EXTRA, new Cantidad(4), precioPublico);
        assertThat(precioSinDescuento.getValor()).isEqualByComparingTo("3.50");
    }

    @Test
    void clienteEspecialSinDescuentoConfigSiemprePagaPrecioEspecial() {
        PrecioEspecial precioEspecial = new PrecioEspecial(
                Precio.de("3.50"), Precio.de("2.80"));
        Cliente cliente = new Cliente(1L, "Tienda Gomez", TipoCliente.ESPECIAL,
                precioEspecial, null);

        Precio precio = cliente.calcularPrecio(TipoProducto.EXTRA, new Cantidad(10), precioPublico);
        assertThat(precio.getValor()).isEqualByComparingTo("3.50");
    }

    @Test
    void crearClienteEspecialSinPrecioLanzaExcepcion() {
        assertThatThrownBy(() ->
                new Cliente(1L, "Sin Precio", TipoCliente.ESPECIAL, null, null))
                .isInstanceOf(ClienteIncompletoException.class)
                .hasMessageContaining("precio especial");
    }

    @Test
    void crearClienteNormalSinPrecioEspecialEsValido() {
        assertThatNoException().isThrownBy(() ->
                new Cliente(1L, "Cliente Normal", TipoCliente.NORMAL, null, null));
    }

    @Test
    void conPrecioEspecialRetornaClienteConNuevoPrecio() {
        PrecioEspecial precioInicial = new PrecioEspecial(Precio.de("3.50"), Precio.de("2.80"));
        Cliente cliente = new Cliente(1L, "Bodega", TipoCliente.ESPECIAL, precioInicial, null);

        PrecioEspecial nuevoPrecio = new PrecioEspecial(Precio.de("3.00"), Precio.de("2.50"));
        Cliente actualizado = cliente.conPrecioEspecial(nuevoPrecio);

        assertThat(actualizado.getPrecioEspecial().getPrecioExtra().getValor())
                .isEqualByComparingTo("3.00");
        assertThat(actualizado.getId()).isEqualTo(cliente.getId());
    }
}
