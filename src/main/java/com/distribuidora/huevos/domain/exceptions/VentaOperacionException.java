package com.distribuidora.huevos.domain.exceptions;

public class VentaOperacionException extends RuntimeException {

    public VentaOperacionException(String mensaje) {
        super(mensaje);
    }
}
