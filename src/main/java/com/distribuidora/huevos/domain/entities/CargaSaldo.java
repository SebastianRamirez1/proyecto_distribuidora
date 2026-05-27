package com.distribuidora.huevos.domain.entities;

import com.distribuidora.huevos.domain.valueobjects.Dinero;

import java.time.LocalDateTime;

/**
 * Registro de un saldo cargado manualmente para un cliente,
 * sin vínculo a una venta real. Usado para migrar deudas de cuadernos
 * físicos al sistema digital o para ajustes manuales justificados.
 */
public class CargaSaldo {

    private final Long          id;
    private final Long          clienteId;
    private final Dinero        monto;
    private final String        descripcion;
    private final LocalDateTime fecha;

    public CargaSaldo(Long id, Long clienteId, Dinero monto,
                      String descripcion, LocalDateTime fecha) {
        this.id          = id;
        this.clienteId   = clienteId;
        this.monto       = monto;
        this.descripcion = descripcion;
        this.fecha       = fecha;
    }

    /** Factory para registrar una nueva carga (id asignado por la BD). */
    public static CargaSaldo nuevo(Long clienteId, Dinero monto, String descripcion) {
        return new CargaSaldo(null, clienteId, monto, descripcion, LocalDateTime.now());
    }

    public Long          getId()          { return id; }
    public Long          getClienteId()   { return clienteId; }
    public Dinero        getMonto()       { return monto; }
    public String        getDescripcion() { return descripcion; }
    public LocalDateTime getFecha()       { return fecha; }
}
