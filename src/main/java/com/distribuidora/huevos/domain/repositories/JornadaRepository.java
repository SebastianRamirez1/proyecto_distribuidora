package com.distribuidora.huevos.domain.repositories;

import com.distribuidora.huevos.domain.entities.Jornada;

import java.util.Optional;

public interface JornadaRepository {
    Optional<Jornada> findActiva();
    Jornada save(Jornada jornada);
}
