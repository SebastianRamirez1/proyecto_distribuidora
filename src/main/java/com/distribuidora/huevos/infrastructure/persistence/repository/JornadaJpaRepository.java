package com.distribuidora.huevos.infrastructure.persistence.repository;

import com.distribuidora.huevos.domain.enums.EstadoJornada;
import com.distribuidora.huevos.infrastructure.persistence.entity.JornadaJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JornadaJpaRepository extends JpaRepository<JornadaJpaEntity, Long> {
    Optional<JornadaJpaEntity> findByEstado(EstadoJornada estado);
}
