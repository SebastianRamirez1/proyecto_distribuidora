package com.distribuidora.huevos.domain.valueobjects;

import com.distribuidora.huevos.domain.enums.TipoProducto;

import java.util.Objects;

public final class PrecioCosto {

    private final Long id;
    private final Precio costoExtra;
    private final Precio costoAA;
    private final Precio costoA;
    private final Precio costoB;

    public PrecioCosto(Long id, Precio costoExtra, Precio costoAA, Precio costoA, Precio costoB) {
        Objects.requireNonNull(costoExtra, "El costo EXTRA no puede ser null");
        Objects.requireNonNull(costoAA,    "El costo AA no puede ser null");
        Objects.requireNonNull(costoA,     "El costo A no puede ser null");
        Objects.requireNonNull(costoB,     "El costo B no puede ser null");
        this.id       = id;
        this.costoExtra = costoExtra;
        this.costoAA    = costoAA;
        this.costoA     = costoA;
        this.costoB     = costoB;
    }

    public Precio obtenerCosto(TipoProducto tipo) {
        return switch (tipo) {
            case EXTRA       -> costoExtra;
            case AA          -> costoAA;
            case A           -> costoA;
            case B           -> costoB;
            case EXTRA_MEDIA -> costoExtra.dividir(2);
            case AA_MEDIA    -> costoAA.dividir(2);
        };
    }

    public PrecioCosto conNuevosCostos(Precio nuevoExtra, Precio nuevoAA,
                                       Precio nuevoA, Precio nuevoB) {
        return new PrecioCosto(this.id, nuevoExtra, nuevoAA, nuevoA, nuevoB);
    }

    public Long getId()           { return id; }
    public Precio getCostoExtra() { return costoExtra; }
    public Precio getCostoAA()    { return costoAA; }
    public Precio getCostoA()     { return costoA; }
    public Precio getCostoB()     { return costoB; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PrecioCosto)) return false;
        PrecioCosto that = (PrecioCosto) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
