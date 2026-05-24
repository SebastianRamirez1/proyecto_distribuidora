package com.distribuidora.huevos.domain.entities;

import com.distribuidora.huevos.domain.enums.TipoPago;
import com.distribuidora.huevos.domain.enums.TipoProducto;
import com.distribuidora.huevos.domain.valueobjects.Cantidad;
import com.distribuidora.huevos.domain.valueobjects.Dinero;
import com.distribuidora.huevos.domain.valueobjects.Precio;

import java.time.LocalDateTime;
import java.util.Objects;

// Venta es inmutable: todos los campos son final, sin setters.
// El precioUnitario es calculado por el dominio antes de construir esta entidad.
public final class Venta {

    private final Long id;
    private final Cliente cliente;
    private final TipoProducto tipoProducto;
    private final Cantidad cantidad;
    private final Precio precioUnitario;
    private final TipoPago tipoPago;
    private final LocalDateTime fecha;

    public Venta(Long id, Cliente cliente, TipoProducto tipoProducto,
                 Cantidad cantidad, Precio precioUnitario,
                 TipoPago tipoPago, LocalDateTime fecha) {
        Objects.requireNonNull(cliente, "El cliente de la venta no puede ser null");
        Objects.requireNonNull(tipoProducto, "El tipo de producto no puede ser null");
        Objects.requireNonNull(cantidad, "La cantidad no puede ser null");
        Objects.requireNonNull(precioUnitario, "El precio unitario no puede ser null");
        Objects.requireNonNull(tipoPago, "El tipo de pago no puede ser null");
        Objects.requireNonNull(fecha, "La fecha de la venta no puede ser null");
        this.id = id;
        this.cliente = cliente;
        this.tipoProducto = tipoProducto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.tipoPago = tipoPago;
        this.fecha = fecha;
    }

    public Dinero calcularTotal() {
        Precio total = precioUnitario.multiplicar(cantidad.getValor());
        return Dinero.de(total.getValor());
    }

    public Long getId() {
        return id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public TipoProducto getTipoProducto() {
        return tipoProducto;
    }

    public Cantidad getCantidad() {
        return cantidad;
    }

    public Precio getPrecioUnitario() {
        return precioUnitario;
    }

    public TipoPago getTipoPago() {
        return tipoPago;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Venta)) return false;
        Venta venta = (Venta) o;
        return Objects.equals(id, venta.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
