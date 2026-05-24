package com.distribuidora.huevos.domain.exceptions;

public class CantidadInvalidaException extends RuntimeException {

    public CantidadInvalidaException(String mensaje) {
        super(mensaje);
    }
}
