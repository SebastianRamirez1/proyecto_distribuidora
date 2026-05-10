package com.distribuidora.huevos.domain.repositories;

import com.distribuidora.huevos.domain.entities.Inventario;

public interface InventarioRepository {

    // Retorna la fila única de inventario (creada por schema.sql)
    Inventario findUnico();

    Inventario save(Inventario inventario);
}
