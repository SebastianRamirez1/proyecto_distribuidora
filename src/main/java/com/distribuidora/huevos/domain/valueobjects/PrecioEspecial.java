package com.distribuidora.huevos.domain.valueobjects;

import com.distribuidora.huevos.domain.enums.TipoProducto;

import java.util.Objects;

public final class PrecioEspecial {

    private final Precio precioExtra;
    private final Precio precioAA;
    private final Precio precioA;
    private final Precio precioB;

    public PrecioEspecial(Precio precioExtra, Precio precioAA, Precio precioA, Precio precioB) {
        Objects.requireNonNull(precioExtra, "El precio especial EXTRA no puede ser null");
        Objects.requireNonNull(precioAA,    "El precio especial AA no puede ser null");
        Objects.requireNonNull(precioA,     "El precio especial A no puede ser null");
        Objects.requireNonNull(precioB,     "El precio especial B no puede ser null");
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

    public Precio getPrecioExtra() { return precioExtra; }
    public Precio getPrecioAA()    { return precioAA; }
    public Precio getPrecioA()     { return precioA; }
    public Precio getPrecioB()     { return precioB; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PrecioEspecial)) return false;
        PrecioEspecial that = (PrecioEspecial) o;
        return Objects.equals(precioExtra, that.precioExtra) &&
               Objects.equals(precioAA,    that.precioAA)    &&
               Objects.equals(precioA,     that.precioA)     &&
               Objects.equals(precioB,     that.precioB);
    }

    @Override
    public int hashCode() {
        return Objects.hash(precioExtra, precioAA, precioA, precioB);
    }
}
