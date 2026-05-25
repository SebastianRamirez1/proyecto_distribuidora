package com.distribuidora.huevos.domain.repositories;

import com.distribuidora.huevos.domain.entities.ConfiguracionFactura;

public interface ConfiguracionFacturaRepository {

    /** Retorna la única fila de configuración (singleton). */
    ConfiguracionFactura findUnica();

    ConfiguracionFactura save(ConfiguracionFactura config);
}
