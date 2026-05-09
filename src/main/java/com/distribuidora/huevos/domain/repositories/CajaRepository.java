package com.distribuidora.huevos.domain.repositories;

import com.distribuidora.huevos.domain.entities.Caja;

import java.time.LocalDate;
import java.util.Optional;

public interface CajaRepository {

    Optional<Caja> findByFecha(LocalDate fecha);

    Caja save(Caja caja);
}
