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

    @Column(name = "precio_especial_extra", precision = 12, scale = 2)
    private BigDecimal precioEspecialExtra;

    @Column(name = "precio_especial_normal", precision = 12, scale = 2)
    private BigDecimal precioEspecialNormal;

    @Column(name = "descuento_desde_canastas")
    private Integer descuentoDesdeCanastas;

    @Column(name = "descuento_precio_extra", precision = 12, scale = 2)
    private BigDecimal descuentoPrecioExtra;

    @Column(name = "descuento_precio_normal", precision = 12, scale = 2)
    private BigDecimal descuentoPrecioNormal;
}
