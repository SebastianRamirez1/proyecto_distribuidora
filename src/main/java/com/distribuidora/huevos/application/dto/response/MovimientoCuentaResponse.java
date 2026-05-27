package com.distribuidora.huevos.application.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Representa una línea del estado de cuenta de un cliente.
 * Puede ser una deuda (venta fiado o carga manual) o un pago (abono).
 */
public class MovimientoCuentaResponse {

    private LocalDateTime fecha;

    /**
     * Tipo de movimiento:
     *   MIGRACION   — saldo cargado manualmente desde cuaderno
     *   VENTA_FIADO — venta registrada en el sistema con pago fiado
     *   ABONO       — pago parcial o total realizado por el cliente
     */
    private String tipo;

    private String     descripcion;
    private BigDecimal monto;

    /** true = genera deuda (débito); false = reduce deuda (crédito). */
    private boolean esDebito;

    public MovimientoCuentaResponse() {}

    public MovimientoCuentaResponse(LocalDateTime fecha, String tipo,
                                     String descripcion, BigDecimal monto,
                                     boolean esDebito) {
        this.fecha       = fecha;
        this.tipo        = tipo;
        this.descripcion = descripcion;
        this.monto       = monto;
        this.esDebito    = esDebito;
    }

    public LocalDateTime getFecha()              { return fecha; }
    public void setFecha(LocalDateTime fecha)    { this.fecha = fecha; }

    public String getTipo()                      { return tipo; }
    public void setTipo(String tipo)             { this.tipo = tipo; }

    public String getDescripcion()                       { return descripcion; }
    public void setDescripcion(String descripcion)       { this.descripcion = descripcion; }

    public BigDecimal getMonto()                 { return monto; }
    public void setMonto(BigDecimal monto)       { this.monto = monto; }

    public boolean isEsDebito()                  { return esDebito; }
    public void setEsDebito(boolean esDebito)    { this.esDebito = esDebito; }
}
