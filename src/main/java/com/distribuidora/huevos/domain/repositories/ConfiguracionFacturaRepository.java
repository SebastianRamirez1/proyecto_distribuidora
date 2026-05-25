package com.distribuidora.huevos.domain.repositories;

import com.distribuidora.huevos.domain.entities.ConfiguracionFactura;

public interface ConfiguracionFacturaRepository {

    /** Retorna la única fila de configuración (solo lectura). */
    ConfiguracionFactura findUnica();

    /**
     * Retorna la configuración con bloqueo pesimista de escritura.
     * Usar SOLO dentro de una transacción activa para garantizar
     * que el consecutivo de facturación no se duplique en concurrencia.
     */
    ConfiguracionFactura findUnicaParaActualizar();

    ConfiguracionFactura save(ConfiguracionFactura config);
}
