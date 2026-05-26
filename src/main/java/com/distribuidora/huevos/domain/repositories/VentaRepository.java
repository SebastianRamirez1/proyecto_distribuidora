package com.distribuidora.huevos.domain.repositories;

import com.distribuidora.huevos.domain.entities.Venta;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface VentaRepository {

    Venta save(Venta venta);

    List<Venta> findByFecha(LocalDate fecha);

    Optional<Venta> findById(Long id);

    BigDecimal calcularGananciaPorFecha(LocalDate fecha);

    boolean existsByClienteId(Long clienteId);

    void deleteByClienteId(Long clienteId);
}
