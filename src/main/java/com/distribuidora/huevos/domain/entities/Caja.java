package com.distribuidora.huevos.domain.entities;

import com.distribuidora.huevos.domain.enums.TipoPago;
import com.distribuidora.huevos.domain.valueobjects.Dinero;

import java.time.LocalDate;

public class Caja {

    private Long id;
    private final LocalDate fecha;
    private Dinero totalEfectivo;
    private Dinero totalTransferencia;
    private Dinero totalFiado;
    private Dinero totalAbonos;

    public Caja(Long id, LocalDate fecha,
                Dinero totalEfectivo, Dinero totalTransferencia,
                Dinero totalFiado, Dinero totalAbonos) {
        this.id = id;
        this.fecha = fecha;
        this.totalEfectivo = totalEfectivo;
        this.totalTransferencia = totalTransferencia;
        this.totalFiado = totalFiado;
        this.totalAbonos = totalAbonos;
    }

    public static Caja nueva(LocalDate fecha) {
        return new Caja(null, fecha,
                Dinero.cero(), Dinero.cero(), Dinero.cero(), Dinero.cero());
    }

    public void registrarPago(TipoPago tipoPago, Dinero monto) {
        switch (tipoPago) {
            case EFECTIVO      -> this.totalEfectivo      = this.totalEfectivo.sumar(monto);
            case TRANSFERENCIA -> this.totalTransferencia = this.totalTransferencia.sumar(monto);
            case FIADO         -> this.totalFiado         = this.totalFiado.sumar(monto);
            case ABONO         -> this.totalAbonos        = this.totalAbonos.sumar(monto);
        }
    }

    /**
     * Registra un abono de deuda:
     * - Suma al efectivo/transferencia (dinero físico que entró en caja).
     * - También suma a totalAbonos como indicador de deuda cobrada hoy.
     * - totalCobrado NO incluye totalAbonos para evitar doble conteo.
     */
    public void registrarAbono(TipoPago medioPago, Dinero monto) {
        switch (medioPago) {
            case EFECTIVO      -> this.totalEfectivo      = this.totalEfectivo.sumar(monto);
            case TRANSFERENCIA -> this.totalTransferencia = this.totalTransferencia.sumar(monto);
            default -> {}  // no aplica otro medio para abonos
        }
        this.totalAbonos = this.totalAbonos.sumar(monto);
    }

    public void revertirPago(TipoPago tipoPago, Dinero monto) {
        switch (tipoPago) {
            case EFECTIVO      -> this.totalEfectivo      = this.totalEfectivo.restar(monto);
            case TRANSFERENCIA -> this.totalTransferencia = this.totalTransferencia.restar(monto);
            case FIADO         -> this.totalFiado         = this.totalFiado.restar(monto);
            case ABONO         -> this.totalAbonos        = this.totalAbonos.restar(monto);
        }
    }

    // Total de dinero físicamente recibido (efectivo + transferencia).
    // Los abonos ya están incluidos en esos dos cajones, así que NO se suman
    // de nuevo desde totalAbonos para evitar doble conteo.
    public Dinero calcularTotalCobrado() {
        return totalEfectivo.sumar(totalTransferencia);
    }

    public Long getId() {
        return id;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public Dinero getTotalEfectivo() {
        return totalEfectivo;
    }

    public Dinero getTotalTransferencia() {
        return totalTransferencia;
    }

    public Dinero getTotalFiado() {
        return totalFiado;
    }

    public Dinero getTotalAbonos() {
        return totalAbonos;
    }
}
