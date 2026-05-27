package com.distribuidora.huevos.domain.repositories;

import com.distribuidora.huevos.domain.entities.CargaSaldo;

import java.util.List;

public interface CargaSaldoRepository {

    CargaSaldo save(CargaSaldo cargaSaldo);

    /** Cargas del cliente ordenadas de la más antigua a la más reciente. */
    List<CargaSaldo> findByClienteIdOrderByFechaAsc(Long clienteId);

    void deleteByClienteId(Long clienteId);
}
