package com.distribuidora.huevos.infrastructure.persistence.repository;

import com.distribuidora.huevos.infrastructure.persistence.entity.VentaJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface VentaJpaRepository extends JpaRepository<VentaJpaEntity, Long> {

    // DECISIÓN: se usa between para que funcione en PostgreSQL y H2 sin depender del CAST
    @Query("SELECT v FROM VentaJpaEntity v WHERE v.fecha >= :inicio AND v.fecha < :fin")
    List<VentaJpaEntity> findByFechaRange(
            @Param("inicio") java.time.LocalDateTime inicio,
            @Param("fin") java.time.LocalDateTime fin);

    // Método de conveniencia usado por el repositorio
    default List<VentaJpaEntity> findByFecha(LocalDate fecha) {
        return findByFechaRange(fecha.atStartOfDay(), fecha.plusDays(1).atStartOfDay());
    }
}
