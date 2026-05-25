package com.distribuidora.huevos.domain.entities;

import com.distribuidora.huevos.domain.enums.EstadoFactura;
import com.distribuidora.huevos.domain.enums.TipoFactura;
import com.distribuidora.huevos.domain.enums.TipoPago;
import com.distribuidora.huevos.domain.enums.TipoProducto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Factura {

    private final Long id;
    private final String numero;
    private final Long ventaId;          // null si se genera sin venta directa
    private final Long clienteId;
    private final LocalDateTime fechaEmision;
    private final TipoFactura tipo;
    private final EstadoFactura estado;
    private final String nombreCliente;
    private final String nitCliente;
    private final TipoProducto tipoProducto;
    private final int cantidad;
    private final BigDecimal precioUnitario;
    private final BigDecimal total;
    private final TipoPago tipoPago;

    public Factura(Long id, String numero, Long ventaId, Long clienteId,
                   LocalDateTime fechaEmision, TipoFactura tipo, EstadoFactura estado,
                   String nombreCliente, String nitCliente,
                   TipoProducto tipoProducto, int cantidad,
                   BigDecimal precioUnitario, BigDecimal total, TipoPago tipoPago) {
        this.id = id;
        this.numero = numero;
        this.ventaId = ventaId;
        this.clienteId = clienteId;
        this.fechaEmision = fechaEmision;
        this.tipo = tipo;
        this.estado = estado;
        this.nombreCliente = nombreCliente;
        this.nitCliente = nitCliente;
        this.tipoProducto = tipoProducto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.total = total;
        this.tipoPago = tipoPago;
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    public Long getId() { return id; }
    public String getNumero() { return numero; }
    public Long getVentaId() { return ventaId; }
    public Long getClienteId() { return clienteId; }
    public LocalDateTime getFechaEmision() { return fechaEmision; }
    public TipoFactura getTipo() { return tipo; }
    public EstadoFactura getEstado() { return estado; }
    public String getNombreCliente() { return nombreCliente; }
    public String getNitCliente() { return nitCliente; }
    public TipoProducto getTipoProducto() { return tipoProducto; }
    public int getCantidad() { return cantidad; }
    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public BigDecimal getTotal() { return total; }
    public TipoPago getTipoPago() { return tipoPago; }
}
