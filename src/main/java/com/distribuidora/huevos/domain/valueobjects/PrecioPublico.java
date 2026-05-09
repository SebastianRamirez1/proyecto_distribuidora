package com.distribuidora.huevos.domain.valueobjects;

import com.distribuidora.huevos.domain.enums.TipoProducto;

import java.util.Objects;

// DECISIÓN: PrecioPublico es una entidad de configuración global (fila única en BD).
// Se modela como clase de valor en el dominio para garantizar inmutabilidad.
public final class PrecioPublico {

    private final Long id;
    private final Precio precioExtra;
    private final Precio precioNormal;

    public PrecioPublico(Long id, Precio precioExtra, Precio precioNormal) {
        Objects.requireNonNull(precioExtra, "El precio público EXTRA no puede ser null");
        Objects.requireNonNull(precioNormal, "El precio público NORMAL no puede ser null");
        this.id = id;
        this.precioExtra = precioExtra;
        this.precioNormal = precioNormal;
    }

    public Precio obtenerPrecio(TipoProducto tipo) {
        return tipo == TipoProducto.EXTRA ? precioExtra : precioNormal;
    }

    public PrecioPublico conNuevosPrecios(Precio nuevoExtra, Precio nuevoNormal) {
        return new PrecioPublico(this.id, nuevoExtra, nuevoNormal);
    }

    public Long getId() {
        return id;
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
        if (!(o instanceof PrecioPublico)) return false;
        PrecioPublico that = (PrecioPublico) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
