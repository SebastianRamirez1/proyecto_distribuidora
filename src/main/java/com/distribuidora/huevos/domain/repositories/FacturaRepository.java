package com.distribuidora.huevos.domain.repositories;

import com.distribuidora.huevos.domain.entities.Factura;

import java.util.List;
import java.util.Optional;

public interface FacturaRepository {

    Factura save(Factura factura);

    Optional<Factura> findById(Long id);

    Optional<Factura> findByVentaId(Long ventaId);

    List<Factura> findAllOrderByFechaDesc();

    List<Factura> findByClienteIdOrderByFechaDesc(Long clienteId);
}
