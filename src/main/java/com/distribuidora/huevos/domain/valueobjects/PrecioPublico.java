package com.distribuidora.huevos.domain.valueobjects;

import com.distribuidora.huevos.domain.enums.TipoProducto;

import java.util.Objects;

public final class PrecioPublico {

    private final Long id;
    private final Precio precioExtra;
    private final Precio precioAA;
    private final Precio precioA;
    private final Precio precioB;

    public PrecioPublico(Long id, Precio precioExtra, Precio precioAA, Precio precioA, Precio precioB) {
        Objects.requireNonNull(precioExtra, "El precio público EXTRA no puede ser null");
        Objects.requireNonNull(precioAA,    "El precio público AA no puede ser null");
        Objects.requireNonNull(precioA,     "El precio público A no puede ser null");
        Objects.requireNonNull(precioB,     "El precio público B no puede ser null");
        this.id = id;
        this.precioExtra = precioExtra;
        this.precioAA = precioAA;
        this.precioA  = precioA;
        this.precioB  = precioB;
    }

    public Precio obtenerPrecio(TipoProducto tipo) {
        return switch (tipo) {
            case EXTRA -> precioExtra;
            case AA    -> precioAA;
            case A     -> precioA;
            case B     -> precioB;
        };
    }

    public PrecioPublico conNuevosPrecios(Precio nuevoExtra, Precio nuevoAA, Precio nuevoA, Precio nuevoB) {
        return new PrecioPublico(this.id, nuevoExtra, nuevoAA, nuevoA, nuevoB);
    }

    public Long getId()           { return id; }
    public Precio getPrecioExtra() { return precioExtra; }
    public Precio getPrecioAA()    { return precioAA; }
    public Precio getPrecioA()     { return precioA; }
    public Precio getPrecioB()     { return precioB; }

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
