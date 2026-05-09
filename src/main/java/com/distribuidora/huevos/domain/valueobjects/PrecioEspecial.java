package com.distribuidora.huevos.domain.valueobjects;

import com.distribuidora.huevos.domain.enums.TipoProducto;

import java.util.Objects;

public final class PrecioEspecial {

    private final Precio precioExtra;
    private final Precio precioNormal;

    public PrecioEspecial(Precio precioExtra, Precio precioNormal) {
        Objects.requireNonNull(precioExtra, "El precio especial EXTRA no puede ser null");
        Objects.requireNonNull(precioNormal, "El precio especial NORMAL no puede ser null");
        this.precioExtra = precioExtra;
        this.precioNormal = precioNormal;
    }

    public Precio obtenerPrecio(TipoProducto tipo) {
        return tipo == TipoProducto.EXTRA ? precioExtra : precioNormal;
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
        if (!(o instanceof PrecioEspecial)) return false;
        PrecioEspecial that = (PrecioEspecial) o;
        return Objects.equals(precioExtra, that.precioExtra) &&
               Objects.equals(precioNormal, that.precioNormal);
    }

    @Override
    public int hashCode() {
        return Objects.hash(precioExtra, precioNormal);
    }
}
