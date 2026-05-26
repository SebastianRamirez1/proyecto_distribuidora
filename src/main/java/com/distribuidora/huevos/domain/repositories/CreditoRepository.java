package com.distribuidora.huevos.domain.repositories;

import com.distribuidora.huevos.domain.entities.Credito;

import java.util.List;
import java.util.Optional;

public interface CreditoRepository {

    Optional<Credito> findByClienteId(Long clienteId);

    /** Todos los créditos con saldo pendiente > 0, ordenados por saldo desc. */
    List<Credito> findDeudores();

    Credito save(Credito credito);

    void deleteByClienteId(Long clienteId);
}
