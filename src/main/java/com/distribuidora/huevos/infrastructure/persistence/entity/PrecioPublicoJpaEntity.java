package com.distribuidora.huevos.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "precio_publico")
@Getter
@Setter
@NoArgsConstructor
public class PrecioPublicoJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "precio_extra", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioExtra;

    @Column(name = "precio_normal", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioNormal;
}
