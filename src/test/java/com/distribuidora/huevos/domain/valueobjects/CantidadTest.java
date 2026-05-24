package com.distribuidora.huevos.domain.valueobjects;

import com.distribuidora.huevos.domain.exceptions.CantidadInvalidaException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class CantidadTest {

    @Test
    void cantidadPositivaSeCreaSinExcepcion() {
        Cantidad cantidad = new Cantidad(5);
        assertThat(cantidad.getValor()).isEqualTo(5);
    }

    @Test
    void cantidadCeroLanzaExcepcion() {
        assertThatThrownBy(() -> new Cantidad(0))
                .isInstanceOf(CantidadInvalidaException.class)
                .hasMessageContaining("mayor a 0");
    }

    @Test
    void cantidadNegativaLanzaExcepcion() {
        assertThatThrownBy(() -> new Cantidad(-3))
                .isInstanceOf(CantidadInvalidaException.class)
                .hasMessageContaining("mayor a 0");
    }

    @Test
    void esMayorOIgualAReturnaTrueParaCantidadIgual() {
        Cantidad cinco = new Cantidad(5);
        assertThat(cinco.esMayorOIgualA(new Cantidad(5))).isTrue();
    }

    @Test
    void esMayorOIgualAReturnaTrueParaCantidadMayor() {
        Cantidad diez = new Cantidad(10);
        assertThat(diez.esMayorOIgualA(new Cantidad(5))).isTrue();
    }

    @Test
    void esMayorOIgualAReturnsFalseParaCantidadMenor() {
        Cantidad tres = new Cantidad(3);
        assertThat(tres.esMayorOIgualA(new Cantidad(5))).isFalse();
    }
}
