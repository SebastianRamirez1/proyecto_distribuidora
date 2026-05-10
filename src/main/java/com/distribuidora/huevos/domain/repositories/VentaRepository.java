package com.distribuidora.huevos.domain.repositories;

import com.distribuidora.huevos.domain.entities.Venta;

import java.time.LocalDate;
import java.util.List;

public interface VentaRepository {

    Venta save(Venta venta);

    List<Venta> findByFecha(LocalDate fecha);
}
