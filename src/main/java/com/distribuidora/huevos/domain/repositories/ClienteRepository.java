package com.distribuidora.huevos.domain.repositories;

import com.distribuidora.huevos.domain.entities.Cliente;

import java.util.List;
import java.util.Optional;

public interface ClienteRepository {

    Cliente save(Cliente cliente);

    Optional<Cliente> findById(Long id);

    List<Cliente> findAll();
}
