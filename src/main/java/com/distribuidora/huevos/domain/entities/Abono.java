package com.distribuidora.huevos.domain.entities;

import com.distribuidora.huevos.domain.enums.TipoPago;
import com.distribuidora.huevos.domain.valueobjects.Dinero;

import java.time.LocalDateTime;

public class Abono {

    private final Long id;
    private final Long clienteId;
    private final Dinero monto;
    private final TipoPago medioPago;
    private final LocalDateTime fecha;

    public Abono(Long id, Long clienteId, Dinero monto, TipoPago medioPago, LocalDateTime fecha) {
        this.id        = id;
        this.clienteId = clienteId;
        this.monto     = monto;
        this.medioPago = medioPago;
        this.fecha     = fecha;
    }

    /** Factory para registrar un abono nuevo (id asignado por la BD). */
    public static Abono nuevo(Long clienteId, Dinero monto, TipoPago medioPago) {
        return new Abono(null, clienteId, monto, medioPago, LocalDateTime.now());
    }

    public Long getId()            { return id; }
    public Long getClienteId()     { return clienteId; }
    public Dinero getMonto()       { return monto; }
    public TipoPago getMedioPago() { return medioPago; }
    public LocalDateTime getFecha(){ return fecha; }
}
