package com.distribuidora.huevos.infrastructure.persistence.entity;

import com.distribuidora.huevos.domain.enums.TipoPago;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "abonos")
@Getter
@Setter
@NoArgsConstructor
public class AbonoJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cliente_id", nullable = false)
    private Long clienteId;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal monto;

    @Enumerated(EnumType.STRING)
    @Column(name = "medio_pago", nullable = false, length = 20)
    private TipoPago medioPago;

    @Column(nullable = false)
    private LocalDateTime fecha;
}
