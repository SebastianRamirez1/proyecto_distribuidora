package com.distribuidora.huevos.domain.valueobjects;

import com.distribuidora.huevos.domain.exceptions.PrecioInvalidoException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public final class Precio {

    private final BigDecimal valor;

    public Precio(BigDecimal valor) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) < 0) {
            throw new PrecioInvalidoException(
                    "El precio no puede ser negativo ni nulo, se recibió: " + valor);
        }
        this.valor = valor.setScale(2, RoundingMode.HALF_UP);
    }

    public static Precio de(BigDecimal valor) {
        return new Precio(valor);
    }

    public static Precio de(String valor) {
        return new Precio(new BigDecimal(valor));
    }

    public static Precio cero() {
        return new Precio(BigDecimal.ZERO);
    }

    public Precio multiplicar(int cantidad) {
        return new Precio(this.valor.multiply(BigDecimal.valueOf(cantidad)));
    }

    public BigDecimal getValor() {
        return valor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Precio)) return false;
        Precio precio = (Precio) o;
        return Objects.equals(valor, precio.valor);
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
