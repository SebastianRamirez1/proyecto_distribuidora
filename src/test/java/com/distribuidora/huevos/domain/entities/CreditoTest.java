package com.distribuidora.huevos.domain.entities;

import com.distribuidora.huevos.domain.enums.TipoCliente;
import com.distribuidora.huevos.domain.valueobjects.Dinero;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

class CreditoTest {

    private Cliente cliente;

    @BeforeEach
    void setUp() {
        cliente = new Cliente(1L, "Juan", TipoCliente.NORMAL, null, null);
    }

    // ── Credito.nuevo ─────────────────────────────────────────────────────────

    @Test
    void nuevoCreditoIniciaMontoPagadoEnCero() {
        Credito credito = Credito.nuevo(cliente, Dinero.de(new BigDecimal("100.00")));
        assertThat(credito.getMontoPagado().getValor()).isEqualByComparingTo("0.00");
    }

    @Test
    void nuevoCreditoTieneIdNulo() {
        Credito credito = Credito.nuevo(cliente, Dinero.de(new BigDecimal("100.00")));
        assertThat(credito.getId()).isNull();
    }

    @Test
    void nuevoCreditoNoEstaSaldado() {
        Credito credito = Credito.nuevo(cliente, Dinero.de(new BigDecimal("100.00")));
        assertThat(credito.estaSaldado()).isFalse();
    }

    // ── saldoPendiente ────────────────────────────────────────────────────────

    @Test
    void saldoPendienteInicialEsIgualAlMontoTotal() {
        Credito credito = Credito.nuevo(cliente, Dinero.de(new BigDecimal("100.00")));
        assertThat(credito.saldoPendiente().getValor()).isEqualByComparingTo("100.00");
    }

    // ── abonar ────────────────────────────────────────────────────────────────

    @Test
    void abonarParcialReduceElSaldoPendiente() {
        Credito credito = Credito.nuevo(cliente, Dinero.de(new BigDecimal("100.00")));
        credito.abonar(Dinero.de(new BigDecimal("40.00")));
        assertThat(credito.saldoPendiente().getValor()).isEqualByComparingTo("60.00");
    }

    @Test
    void abonarElTotalSaldaElCredito() {
        Credito credito = Credito.nuevo(cliente, Dinero.de(new BigDecimal("100.00")));
        credito.abonar(Dinero.de(new BigDecimal("100.00")));
        assertThat(credito.estaSaldado()).isTrue();
    }

    @Test
    void abonarElTotalDejaMontoEnCero() {
        Credito credito = Credito.nuevo(cliente, Dinero.de(new BigDecimal("100.00")));
        credito.abonar(Dinero.de(new BigDecimal("100.00")));
        assertThat(credito.saldoPendiente().getValor()).isEqualByComparingTo("0.00");
    }

    @Test
    void abonarMasDelSaldoLanzaExcepcionConMensajeClaro() {
        Credito credito = Credito.nuevo(cliente, Dinero.de(new BigDecimal("100.00")));
        assertThatThrownBy(() -> credito.abonar(Dinero.de(new BigDecimal("150.00"))))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("excede el saldo pendiente");
    }

    @Test
    void variosAbonosParciales() {
        Credito credito = Credito.nuevo(cliente, Dinero.de(new BigDecimal("200.00")));
        credito.abonar(Dinero.de(new BigDecimal("80.00")));
        credito.abonar(Dinero.de(new BigDecimal("70.00")));
        // 80 + 70 = 150 pagados → saldo = 200 - 150 = 50
        assertThat(credito.saldoPendiente().getValor()).isEqualByComparingTo("50.00");
    }

    // ── agregarDeuda ─────────────────────────────────────────────────────────

    @Test
    void agregarDeudaAumentaMontoTotal() {
        Credito credito = Credito.nuevo(cliente, Dinero.de(new BigDecimal("100.00")));
        credito.agregarDeuda(Dinero.de(new BigDecimal("50.00")));
        assertThat(credito.getMontoTotal().getValor()).isEqualByComparingTo("150.00");
    }

    @Test
    void agregarDeudaAumentaElSaldoPendiente() {
        Credito credito = Credito.nuevo(cliente, Dinero.de(new BigDecimal("100.00")));
        credito.agregarDeuda(Dinero.de(new BigDecimal("50.00")));
        assertThat(credito.saldoPendiente().getValor()).isEqualByComparingTo("150.00");
    }

    // ── revertirDeuda ─────────────────────────────────────────────────────────

    @Test
    void revertirDeudaReduceMontoTotal() {
        Credito credito = Credito.nuevo(cliente, Dinero.de(new BigDecimal("100.00")));
        credito.revertirDeuda(Dinero.de(new BigDecimal("100.00")));
        assertThat(credito.getMontoTotal().getValor()).isEqualByComparingTo("0.00");
    }

    @Test
    void revertirDeudaParcialCalculaSaldoCorrectamente() {
        Credito credito = Credito.nuevo(cliente, Dinero.de(new BigDecimal("200.00")));
        // Abono previo de 80
        credito.abonar(Dinero.de(new BigDecimal("80.00")));
        // Revertir deuda de 100 (venta fiado anulada)
        credito.revertirDeuda(Dinero.de(new BigDecimal("100.00")));
        // montoTotal = 200 - 100 = 100; montoPagado = 80; saldo = 20
        assertThat(credito.saldoPendiente().getValor()).isEqualByComparingTo("20.00");
    }
}
