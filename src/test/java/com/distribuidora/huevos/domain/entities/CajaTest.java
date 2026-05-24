package com.distribuidora.huevos.domain.entities;

import com.distribuidora.huevos.domain.enums.TipoPago;
import com.distribuidora.huevos.domain.valueobjects.Dinero;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests de Caja — entidad central de contabilidad.
 *
 * Regla de negocio clave:
 *   totalCobrado = efectivo + transferencia
 *   Los abonos ya están incluidos en esos dos cajones (efectivo o transferencia),
 *   por lo que totalAbonos es informativo y NO se suma de nuevo en totalCobrado.
 */
class CajaTest {

    private Caja caja;

    @BeforeEach
    void setUp() {
        caja = Caja.nueva(LocalDate.now());
    }

    // ── Caja.nueva ────────────────────────────────────────────────────────────

    @Test
    void nuevaCajaIniciaTodosLosCamposEnCero() {
        assertThat(caja.getTotalEfectivo().getValor()).isEqualByComparingTo("0.00");
        assertThat(caja.getTotalTransferencia().getValor()).isEqualByComparingTo("0.00");
        assertThat(caja.getTotalFiado().getValor()).isEqualByComparingTo("0.00");
        assertThat(caja.getTotalAbonos().getValor()).isEqualByComparingTo("0.00");
    }

    @Test
    void nuevaCajaTieneIdNulo() {
        assertThat(caja.getId()).isNull();
    }

    // ── registrarPago ─────────────────────────────────────────────────────────

    @Test
    void registrarPagoEfectivoSumaAlEfectivo() {
        caja.registrarPago(TipoPago.EFECTIVO, Dinero.de(new BigDecimal("100.00")));
        assertThat(caja.getTotalEfectivo().getValor()).isEqualByComparingTo("100.00");
    }

    @Test
    void registrarPagoTransferenciaSumaATransferencia() {
        caja.registrarPago(TipoPago.TRANSFERENCIA, Dinero.de(new BigDecimal("50.00")));
        assertThat(caja.getTotalTransferencia().getValor()).isEqualByComparingTo("50.00");
    }

    @Test
    void registrarPagoFiadoSumaAlFiado() {
        caja.registrarPago(TipoPago.FIADO, Dinero.de(new BigDecimal("80.00")));
        assertThat(caja.getTotalFiado().getValor()).isEqualByComparingTo("80.00");
    }

    @Test
    void registrarPagoAbonoLegadoSumaAAbonos() {
        // TipoPago.ABONO es el camino de registrarPago (legacy); los abonos reales
        // usan registrarAbono(). Este test garantiza que el switch no rompe ese caso.
        caja.registrarPago(TipoPago.ABONO, Dinero.de(new BigDecimal("30.00")));
        assertThat(caja.getTotalAbonos().getValor()).isEqualByComparingTo("30.00");
    }

    @Test
    void registrarPagoAcumulaCorrectamenteVariosEfectivo() {
        caja.registrarPago(TipoPago.EFECTIVO, Dinero.de(new BigDecimal("30.00")));
        caja.registrarPago(TipoPago.EFECTIVO, Dinero.de(new BigDecimal("20.00")));
        assertThat(caja.getTotalEfectivo().getValor()).isEqualByComparingTo("50.00");
    }

    // ── registrarAbono ────────────────────────────────────────────────────────
    // Regla: un abono en efectivo suma al totalEfectivo Y al totalAbonos.
    // Un abono por transferencia suma al totalTransferencia Y al totalAbonos.

    @Test
    void registrarAbonoEfectivoSumaAEfectivoYAbonos() {
        caja.registrarAbono(TipoPago.EFECTIVO, Dinero.de(new BigDecimal("20000.00")));

        assertThat(caja.getTotalEfectivo().getValor())
                .as("El dinero físico debe reflejarse en el cajón de efectivo")
                .isEqualByComparingTo("20000.00");
        assertThat(caja.getTotalAbonos().getValor())
                .as("Debe quedar registro de la deuda cobrada hoy")
                .isEqualByComparingTo("20000.00");
    }

    @Test
    void registrarAbonoTransferenciaSumaATransferenciaYAbonos() {
        caja.registrarAbono(TipoPago.TRANSFERENCIA, Dinero.de(new BigDecimal("15000.00")));

        assertThat(caja.getTotalTransferencia().getValor()).isEqualByComparingTo("15000.00");
        assertThat(caja.getTotalAbonos().getValor()).isEqualByComparingTo("15000.00");
    }

    @Test
    void registrarAbonoEfectivoNoAfectaTransferencia() {
        caja.registrarAbono(TipoPago.EFECTIVO, Dinero.de(new BigDecimal("10000.00")));
        assertThat(caja.getTotalTransferencia().getValor()).isEqualByComparingTo("0.00");
    }

    @Test
    void registrarAbonoTransferenciaNoAfectaEfectivo() {
        caja.registrarAbono(TipoPago.TRANSFERENCIA, Dinero.de(new BigDecimal("5000.00")));
        assertThat(caja.getTotalEfectivo().getValor()).isEqualByComparingTo("0.00");
    }

    // ── calcularTotalCobrado — regresión anti-doble-conteo ────────────────────

    @Test
    void totalCobradoEsEfectivoMasTransferencia() {
        caja.registrarPago(TipoPago.EFECTIVO,      Dinero.de(new BigDecimal("85000.00")));
        caja.registrarPago(TipoPago.TRANSFERENCIA, Dinero.de(new BigDecimal("15000.00")));

        assertThat(caja.calcularTotalCobrado().getValor()).isEqualByComparingTo("100000.00");
    }

    @Test
    void totalCobradoNoIncluye_Fiado() {
        caja.registrarPago(TipoPago.EFECTIVO, Dinero.de(new BigDecimal("50000.00")));
        caja.registrarPago(TipoPago.FIADO,    Dinero.de(new BigDecimal("30000.00")));

        assertThat(caja.calcularTotalCobrado().getValor())
                .as("El fiado no es dinero cobrado: no debe aparecer en totalCobrado")
                .isEqualByComparingTo("50000.00");
    }

    @Test
    void totalCobradoNoIncluye_Abonos_ParaEvitarDobleConteo() {
        // Escenario real: ventas efectivo $85k + abono en efectivo $20k + $30k fiado.
        // totalEfectivo = 85k + 20k = 105k (el abono ya está dentro del efectivo).
        // totalCobrado debe ser 105k, NO 105k + 20k = 125k.
        caja.registrarPago(TipoPago.EFECTIVO, Dinero.de(new BigDecimal("85000.00")));
        caja.registrarPago(TipoPago.FIADO,    Dinero.de(new BigDecimal("30000.00")));
        caja.registrarAbono(TipoPago.EFECTIVO, Dinero.de(new BigDecimal("20000.00")));

        assertThat(caja.getTotalEfectivo().getValor()).isEqualByComparingTo("105000.00");
        assertThat(caja.calcularTotalCobrado().getValor())
                .as("totalCobrado no debe sumar totalAbonos por segunda vez")
                .isEqualByComparingTo("105000.00");
    }

    @Test
    void totalCobradoEsCeroCuandoCajaEsNueva() {
        assertThat(caja.calcularTotalCobrado().getValor()).isEqualByComparingTo("0.00");
    }

    // ── revertirPago ─────────────────────────────────────────────────────────

    @Test
    void revertirPagoEfectivoDescuentaCorrectamente() {
        caja.registrarPago(TipoPago.EFECTIVO, Dinero.de(new BigDecimal("100.00")));
        caja.revertirPago(TipoPago.EFECTIVO,  Dinero.de(new BigDecimal("100.00")));
        assertThat(caja.getTotalEfectivo().getValor()).isEqualByComparingTo("0.00");
    }

    @Test
    void revertirPagoFiadoDescuentaDelFiado() {
        caja.registrarPago(TipoPago.FIADO, Dinero.de(new BigDecimal("200.00")));
        caja.revertirPago(TipoPago.FIADO,  Dinero.de(new BigDecimal("200.00")));
        assertThat(caja.getTotalFiado().getValor()).isEqualByComparingTo("0.00");
    }

    @Test
    void revertirPagoTransferenciaDescuentaDeTransferencia() {
        caja.registrarPago(TipoPago.TRANSFERENCIA, Dinero.de(new BigDecimal("60.00")));
        caja.revertirPago(TipoPago.TRANSFERENCIA,  Dinero.de(new BigDecimal("40.00")));
        assertThat(caja.getTotalTransferencia().getValor()).isEqualByComparingTo("20.00");
    }
}
