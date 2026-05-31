package com.distribuidora.huevos.domain.enums;

public enum EstadoJornada {
    /** Jornada activa: las ventas/abonos nuevos van aquí por defecto. */
    ABIERTA,
    /** Jornada liquidada pero con ventas rezagadas pendientes.
     *  Coexiste con ABIERTA hasta que se cierra definitivamente. */
    EN_CIERRE,
    /** Jornada completamente cerrada. No acepta más registros. */
    CERRADA
}
