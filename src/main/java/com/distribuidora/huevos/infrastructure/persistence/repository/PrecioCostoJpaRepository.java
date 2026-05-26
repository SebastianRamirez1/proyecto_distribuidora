package com.distribuidora.huevos.infrastructure.persistence.repository;

import com.distribuidora.huevos.infrastructure.persistence.entity.PrecioCostoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PrecioCostoJpaRepository extends JpaRepository<PrecioCostoJpaEntity, Long> {
    Optional<PrecioCostoJpaEntity> findFirstByOrderByIdAsc();
}
