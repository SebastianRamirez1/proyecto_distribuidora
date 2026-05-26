package com.distribuidora.huevos.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "precio_costo")
@Getter
@Setter
@NoArgsConstructor
public class PrecioCostoJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "costo_extra", nullable = false, precision = 12, scale = 2)
    private BigDecimal costoExtra;

    @Column(name = "costo_aa", nullable = false, precision = 12, scale = 2)
    private BigDecimal costoAA;

    @Column(name = "costo_a", nullable = false, precision = 12, scale = 2)
    private BigDecimal costoA;

    @Column(name = "costo_b", nullable = false, precision = 12, scale = 2)
    private BigDecimal costoB;
}
