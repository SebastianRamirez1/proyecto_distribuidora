package com.distribuidora.huevos.domain.valueobjects;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

class DineroTest {

    // ── construcción ─────────────────────────────────────────────────────────

    @Test
    void ceroRetornaValorCero() {
        assertThat(Dinero.cero().getValor()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void deCreaDineroConValorCorrecto() {
        Dinero d = Dinero.de(new BigDecimal("25.50"));
        assertThat(d.getValor()).isEqualByComparingTo("25.50");
    }

    @Test
    void nuloLanzaExcepcion() {
        assertThatThrownBy(() -> new Dinero(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void escalaSeNormalizaA2Decimales() {
        Dinero d = Dinero.de(new BigDecimal("10.5"));
        assertThat(d.getValor().scale()).isEqualTo(2);
    }

    // ── sumar ─────────────────────────────────────────────────────────────────

    @Test
    void sumarDosValoresPositivos() {
        Dinero a = Dinero.de(new BigDecimal("10.00"));
        Dinero b = Dinero.de(new BigDecimal("5.50"));
        assertThat(a.sumar(b).getValor()).isEqualByComparingTo("15.50");
    }

    @Test
    void sumarConCeroDevuelveElMismoValor() {
        Dinero a = Dinero.de(new BigDecimal("100.00"));
        assertThat(a.sumar(Dinero.cero()).getValor()).isEqualByComparingTo("100.00");
    }

    @Test
    void sumarEsAcumulativo() {
        Dinero base = Dinero.cero();
        base = base.sumar(Dinero.de(new BigDecimal("50.00")));
        base = base.sumar(Dinero.de(new BigDecimal("30.00")));
        base = base.sumar(Dinero.de(new BigDecimal("20.00")));
        assertThat(base.getValor()).isEqualByComparingTo("100.00");
    }

    // ── restar ────────────────────────────────────────────────────────────────

    @Test
    void restarValorMenor() {
        Dinero a = Dinero.de(new BigDecimal("20.00"));
        Dinero b = Dinero.de(new BigDecimal("8.00"));
        assertThat(a.restar(b).getValor()).isEqualByComparingTo("12.00");
    }

    @Test
    void restarElMismoValorDaCero() {
        Dinero a = Dinero.de(new BigDecimal("50.00"));
        assertThat(a.restar(a).getValor()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void restarCeroDevuelveElMismoValor() {
        Dinero a = Dinero.de(new BigDecimal("75.00"));
        assertThat(a.restar(Dinero.cero()).getValor()).isEqualByComparingTo("75.00");
    }

    // ── esMayorQue ───────────────────────────────────────────────────────────

    @Test
    void esMayorQueRetornaTrueCuandoEsMayor() {
        assertThat(Dinero.de(new BigDecimal("10.00")).esMayorQue(Dinero.de(new BigDecimal("5.00")))).isTrue();
    }

    @Test
    void esMayorQueRetornaFalseCuandoSonIguales() {
        Dinero a = Dinero.de(new BigDecimal("10.00"));
        assertThat(a.esMayorQue(a)).isFalse();
    }

    @Test
    void esMayorQueRetornaFalseCuandoEsMenor() {
        assertThat(Dinero.de(new BigDecimal("3.00")).esMayorQue(Dinero.de(new BigDecimal("5.00")))).isFalse();
    }

    // ── equals / hashCode ────────────────────────────────────────────────────

    @Test
    void equalsRetornaTruePorValorIgual() {
        Dinero a = Dinero.de(new BigDecimal("10.00"));
        Dinero b = Dinero.de(new BigDecimal("10.00"));
        assertThat(a).isEqualTo(b);
    }

    @Test
    void equalsRetornaFalsePorValoresDiferentes() {
        assertThat(Dinero.de(new BigDecimal("10.00")))
                .isNotEqualTo(Dinero.de(new BigDecimal("10.01")));
    }
}
