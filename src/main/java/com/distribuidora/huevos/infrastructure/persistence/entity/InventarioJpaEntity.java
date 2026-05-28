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

    /** NUMERIC(10,1) — soporta 0.5 para representar media canasta abierta */
    @Column(name = "stock_extra", nullable = false)
    private Double stockExtra;

    /** NUMERIC(10,1) — soporta 0.5 para representar media canasta abierta */
    @Column(name = "stock_aa", nullable = false)
    private Double stockAA;

    @Column(name = "stock_a", nullable = false)
    private Integer stockA;

    @Column(name = "stock_b", nullable = false)
    private Integer stockB;
}
