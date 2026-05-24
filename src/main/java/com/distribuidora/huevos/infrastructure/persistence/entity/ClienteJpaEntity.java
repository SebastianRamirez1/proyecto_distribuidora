package com.distribuidora.huevos.infrastructure.persistence.entity;

import com.distribuidora.huevos.domain.enums.TipoCliente;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "clientes")
@Getter
@Setter
@NoArgsConstructor
public class ClienteJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoCliente tipo;

    // Precios especiales
    @Column(name = "precio_especial_extra", precision = 12, scale = 2)
    private BigDecimal precioEspecialExtra;

    @Column(name = "precio_especial_aa", precision = 12, scale = 2)
    private BigDecimal precioEspecialAA;

    @Column(name = "precio_especial_a", precision = 12, scale = 2)
    private BigDecimal precioEspecialA;

    @Column(name = "precio_especial_b", precision = 12, scale = 2)
    private BigDecimal precioEspecialB;

    // Descuento por volumen
    @Column(name = "descuento_desde_canastas")
    private Integer descuentoDesdeCanastas;

    @Column(name = "descuento_precio_extra", precision = 12, scale = 2)
    private BigDecimal descuentoPrecioExtra;

    @Column(name = "descuento_precio_aa", precision = 12, scale = 2)
    private BigDecimal descuentoPrecioAA;

    @Column(name = "descuento_precio_a", precision = 12, scale = 2)
    private BigDecimal descuentoPrecioA;

    @Column(name = "descuento_precio_b", precision = 12, scale = 2)
    private BigDecimal descuentoPrecioB;
}
