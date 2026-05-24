package com.distribuidora.huevos.domain.repositories;

import com.distribuidora.huevos.domain.entities.Credito;

import java.util.Optional;

public interface CreditoRepository {

    Optional<Credito> findByClienteId(Long clienteId);

    Credito save(Credito credito);
}
