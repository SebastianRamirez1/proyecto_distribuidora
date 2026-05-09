package com.distribuidora.huevos.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "caja")
@Getter
@Setter
@NoArgsConstructor
public class CajaJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private LocalDate fecha;

    @Column(name = "total_efectivo", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalEfectivo;

    @Column(name = "total_transferencia", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalTransferencia;

    @Column(name = "total_fiado", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalFiado;

    @Column(name = "total_abonos", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAbonos;
}
