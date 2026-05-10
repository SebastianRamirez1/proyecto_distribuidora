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
            case EFECTIVO -> this.totalEfectivo = this.totalEfectivo.sumar(monto);
            case TRANSFERENCIA -> this.totalTransferencia = this.totalTransferencia.sumar(monto);
            case FIADO -> this.totalFiado = this.totalFiado.sumar(monto);
            case ABONO -> this.totalAbonos = this.totalAbonos.sumar(monto);
        }
    }

    // Total de dinero físicamente recibido (excluye fiado, que aún no se cobró)
    public Dinero calcularTotalCobrado() {
        return totalEfectivo.sumar(totalTransferencia).sumar(totalAbonos);
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
