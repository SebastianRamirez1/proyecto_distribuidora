package com.distribuidora.huevos.domain.exceptions;

public class ClienteIncompletoException extends RuntimeException {

    public ClienteIncompletoException(String mensaje) {
        super(mensaje);
    }
}
