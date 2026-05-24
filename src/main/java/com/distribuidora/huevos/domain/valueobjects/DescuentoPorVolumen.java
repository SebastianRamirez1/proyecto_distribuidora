package com.distribuidora.huevos.domain.valueobjects;

import com.distribuidora.huevos.domain.enums.TipoProducto;

import java.util.Objects;

public final class DescuentoPorVolumen {

    private final Cantidad desdeCanastas;
    private final Precio precioExtra;
    private final Precio precioAA;
    private final Precio precioA;
    private final Precio precioB;

    public DescuentoPorVolumen(Cantidad desdeCanastas,
                               Precio precioExtra, Precio precioAA,
                               Precio precioA, Precio precioB) {
        Objects.requireNonNull(desdeCanastas, "La cantidad mínima para el descuento no puede ser null");
        Objects.requireNonNull(precioExtra,   "El precio EXTRA con descuento no puede ser null");
        Objects.requireNonNull(precioAA,      "El precio AA con descuento no puede ser null");
        Objects.requireNonNull(precioA,       "El precio A con descuento no puede ser null");
        Objects.requireNonNull(precioB,       "El precio B con descuento no puede ser null");
        this.desdeCanastas = desdeCanastas;
        this.precioExtra = precioExtra;
        this.precioAA = precioAA;
        this.precioA  = precioA;
        this.precioB  = precioB;
    }

    public boolean aplica(Cantidad cantidad) {
        return cantidad.esMayorOIgualA(desdeCanastas);
    }

    public Precio obtenerPrecio(TipoProducto tipo) {
        return switch (tipo) {
            case EXTRA -> precioExtra;
            case AA    -> precioAA;
            case A     -> precioA;
            case B     -> precioB;
        };
    }

    public Cantidad getDesdeCanastas() { return desdeCanastas; }
    public Precio getPrecioExtra()     { return precioExtra; }
    public Precio getPrecioAA()        { return precioAA; }
    public Precio getPrecioA()         { return precioA; }
    public Precio getPrecioB()         { return precioB; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DescuentoPorVolumen)) return false;
        DescuentoPorVolumen that = (DescuentoPorVolumen) o;
        return Objects.equals(desdeCanastas, that.desdeCanastas) &&
               Objects.equals(precioExtra,   that.precioExtra)   &&
               Objects.equals(precioAA,      that.precioAA)      &&
               Objects.equals(precioA,       that.precioA)       &&
               Objects.equals(precioB,       that.precioB);
    }

    @Override
    public int hashCode() {
        return Objects.hash(desdeCanastas, precioExtra, precioAA, precioA, precioB);
    }
}
