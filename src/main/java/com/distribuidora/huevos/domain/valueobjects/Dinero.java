package com.distribuidora.huevos.domain.valueobjects;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public final class Dinero {

    private final BigDecimal valor;

    public Dinero(BigDecimal valor) {
        Objects.requireNonNull(valor, "El valor de Dinero no puede ser null");
        this.valor = valor.setScale(2, RoundingMode.HALF_UP);
    }

    public static Dinero cero() {
        return new Dinero(BigDecimal.ZERO);
    }

    public static Dinero de(BigDecimal valor) {
        return new Dinero(valor);
    }

    public Dinero sumar(Dinero otro) {
        return new Dinero(this.valor.add(otro.valor));
    }

    public Dinero restar(Dinero otro) {
        return new Dinero(this.valor.subtract(otro.valor));
    }

    public boolean esMayorQue(Dinero otro) {
        return this.valor.compareTo(otro.valor) > 0;
    }

    public BigDecimal getValor() {
        return valor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Dinero)) return false;
        Dinero dinero = (Dinero) o;
        return Objects.equals(valor, dinero.valor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valor);
    }

    @Override
    public String toString() {
        return valor.toPlainString();
    }
}
