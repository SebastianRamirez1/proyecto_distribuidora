package com.distribuidora.huevos.domain.repositories;

import com.distribuidora.huevos.domain.valueobjects.PrecioPublico;

public interface PrecioPublicoRepository {

    // Retorna la fila única de precio público (creada por schema.sql)
    PrecioPublico findCurrent();

    PrecioPublico save(PrecioPublico precioPublico);
}
