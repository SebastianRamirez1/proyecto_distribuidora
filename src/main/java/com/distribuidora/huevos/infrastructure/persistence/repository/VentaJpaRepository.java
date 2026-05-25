package com.distribuidora.huevos.infrastructure.persistence.repository;

import com.distribuidora.huevos.infrastructure.persistence.entity.VentaJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface VentaJpaRepository extends JpaRepository<VentaJpaEntity, Long> {

    @Query("SELECT v FROM VentaJpaEntity v WHERE v.fecha >= :inicio AND v.fecha < :fin AND v.anulada = false")
    List<VentaJpaEntity> findByFechaRange(
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin);

    // Solo ventas con costo configurado (> 0) para no inflar la ganancia con ventas sin costo
    @Query("SELECT COALESCE(SUM((v.precioUnitario - v.costoUnitario) * v.cantidad), 0) " +
           "FROM VentaJpaEntity v " +
           "WHERE v.fecha >= :inicio AND v.fecha < :fin " +
           "AND v.anulada = false AND v.costoUnitario IS NOT NULL AND v.costoUnitario > 0")
    BigDecimal calcularGananciaByFechaRange(
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin);

    default List<VentaJpaEntity> findByFecha(LocalDate fecha) {
        return findByFechaRange(fecha.atStartOfDay(), fecha.plusDays(1).atStartOfDay());
    }

    default BigDecimal calcularGananciaPorFecha(LocalDate fecha) {
        return calcularGananciaByFechaRange(fecha.atStartOfDay(), fecha.plusDays(1).atStartOfDay());
    }
}
