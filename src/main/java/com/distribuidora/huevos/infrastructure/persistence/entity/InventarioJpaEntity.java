package com.distribuidora.huevos.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "inventario")
@Getter
@Setter
@NoArgsConstructor
public class InventarioJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "stock_extra", nullable = false)
    private Integer stockExtra;

    @Column(name = "stock_aa", nullable = false)
    private Integer stockAA;

    @Column(name = "stock_a", nullable = false)
    private Integer stockA;

    @Column(name = "stock_b", nullable = false)
    private Integer stockB;
}
