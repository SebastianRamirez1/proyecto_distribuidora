package com.distribuidora.huevos.domain.repositories;

import com.distribuidora.huevos.domain.entities.Abono;

import java.util.List;

public interface AbonoRepository {

    Abono save(Abono abono);

    /** Devuelve los abonos del cliente ordenados del más reciente al más antiguo. */
    List<Abono> findByClienteIdOrderByFechaDesc(Long clienteId);

    void deleteByClienteId(Long clienteId);
}
