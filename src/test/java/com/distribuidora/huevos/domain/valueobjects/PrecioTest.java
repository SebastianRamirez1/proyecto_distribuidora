package com.distribuidora.huevos.domain.valueobjects;

import com.distribuidora.huevos.domain.exceptions.PrecioInvalidoException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

class PrecioTest {

    @Test
    void precioPositivoSeCreaSinExcepcion() {
        Precio precio = Precio.de("10.50");
        assertThat(precio.getValor()).isEqualByComparingTo("10.50");
    }

    @Test
    void precioCeroEsValido() {
        Precio precio = Precio.de("0");
        assertThat(precio.getValor()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void precioNegativoLanzaExcepcion() {
        assertThatThrownBy(() -> Precio.de("-1.00"))
                .isInstanceOf(PrecioInvalidoException.class)
                .hasMessageContaining("negativo");
    }

    @Test
    void precioNuloLanzaExcepcion() {
        assertThatThrownBy(() -> new Precio(null))
                .isInstanceOf(PrecioInvalidoException.class);
    }

    @Test
    void multiplicarCalculaCorrectamente() {
        Precio precio = Precio.de("5.00");
        Precio total = precio.multiplicar(3);
        assertThat(total.getValor()).isEqualByComparingTo("15.00");
    }

    @Test
    void multiplicarPorUnoDevuelveElMismoPrecio() {
        Precio precio = Precio.de("7.50");
        Precio total = precio.multiplicar(1);
        assertThat(total.getValor()).isEqualByComparingTo("7.50");
    }
}
