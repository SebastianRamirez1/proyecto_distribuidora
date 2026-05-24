package com.distribuidora.huevos.domain.valueobjects;

import com.distribuidora.huevos.domain.exceptions.CantidadInvalidaException;

import java.util.Objects;

public final class Cantidad {

    private final int valor;

    public Cantidad(int valor) {
        if (valor <= 0) {
            throw new CantidadInvalidaException(
                    "La cantidad debe ser mayor a 0, se recibió: " + valor);
        }
        this.valor = valor;
    }

    public int getValor() {
        return valor;
    }

    public boolean esMayorOIgualA(Cantidad otra) {
        return this.valor >= otra.valor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cantidad)) return false;
        Cantidad cantidad = (Cantidad) o;
        return valor == cantidad.valor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(valor);
    }

    @Override
    public String toString() {
        return String.valueOf(valor);
    }
}
