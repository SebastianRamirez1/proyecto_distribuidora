package com.distribuidora.huevos.domain.valueobjects;

import com.distribuidora.huevos.domain.enums.TipoProducto;

import java.util.Objects;

public final class DescuentoPorVolumen {

    private final Cantidad desdeCanastas;
    private final Precio precioExtra;
    private final Precio precioNormal;

    public DescuentoPorVolumen(Cantidad desdeCanastas, Precio precioExtra, Precio precioNormal) {
        Objects.requireNonNull(desdeCanastas, "La cantidad mínima para el descuento no puede ser null");
        Objects.requireNonNull(precioExtra, "El precio EXTRA con descuento no puede ser null");
        Objects.requireNonNull(precioNormal, "El precio NORMAL con descuento no puede ser null");
        this.desdeCanastas = desdeCanastas;
        this.precioExtra = precioExtra;
        this.precioNormal = precioNormal;
    }

    public boolean aplica(Cantidad cantidad) {
        return cantidad.esMayorOIgualA(desdeCanastas);
    }

    public Precio obtenerPrecio(TipoProducto tipo) {
        return tipo == TipoProducto.EXTRA ? precioExtra : precioNormal;
    }

    public Cantidad getDesdeCanastas() {
        return desdeCanastas;
    }

    public Precio getPrecioExtra() {
        return precioExtra;
    }

    public Precio getPrecioNormal() {
        return precioNormal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DescuentoPorVolumen)) return false;
        DescuentoPorVolumen that = (DescuentoPorVolumen) o;
        return Objects.equals(desdeCanastas, that.desdeCanastas) &&
               Objects.equals(precioExtra, that.precioExtra) &&
               Objects.equals(precioNormal, that.precioNormal);
    }

    @Override
    public int hashCode() {
        return Objects.hash(desdeCanastas, precioExtra, precioNormal);
    }
}
