package com.distribuidora.huevos.application.dto.response;

import com.distribuidora.huevos.domain.enums.TipoPago;
import com.distribuidora.huevos.domain.enums.TipoProducto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class VentaResponse {

    private Long id;
    private Long clienteId;
    private String nombreCliente;
    private TipoProducto tipoProducto;
    private int cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal total;
    private TipoPago tipoPago;
    private LocalDateTime fecha;

    public VentaResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }

    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }

    public TipoProducto getTipoProducto() { return tipoProducto; }
    public void setTipoProducto(TipoProducto tipoProducto) { this.tipoProducto = tipoProducto; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public TipoPago getTipoPago() { return tipoPago; }
    public void setTipoPago(TipoPago tipoPago) { this.tipoPago = tipoPago; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
}
