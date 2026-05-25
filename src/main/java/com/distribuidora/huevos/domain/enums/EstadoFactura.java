package com.distribuidora.huevos.domain.enums;

public enum EstadoFactura {
    EMITIDA,       // PDF generado
    ENVIADA_DIAN,  // Enviada y aceptada por DIAN (factura electrónica)
    ANULADA        // Anulada
}
