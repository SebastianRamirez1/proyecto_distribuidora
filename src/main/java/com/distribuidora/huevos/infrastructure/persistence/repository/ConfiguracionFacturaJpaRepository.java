package com.distribuidora.huevos.infrastructure.persistence.repository;

import com.distribuidora.huevos.infrastructure.persistence.entity.ConfiguracionFacturaJpaEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ConfiguracionFacturaJpaRepository
        extends JpaRepository<ConfiguracionFacturaJpaEntity, Long> {

    // Lock pesimista para garantizar que el consecutivo no se duplique
    // en caso de dos peticiones simultáneas de facturación.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM ConfiguracionFacturaJpaEntity c ORDER BY c.id ASC LIMIT 1")
    Optional<ConfiguracionFacturaJpaEntity> findFirstForUpdate();

    @Query("SELECT c FROM ConfiguracionFacturaJpaEntity c ORDER BY c.id ASC LIMIT 1")
    Optional<ConfiguracionFacturaJpaEntity> findFirst();
}
