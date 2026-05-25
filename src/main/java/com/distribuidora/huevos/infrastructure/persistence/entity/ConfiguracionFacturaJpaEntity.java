package com.distribuidora.huevos.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "configuracion_factura")
@Getter
@Setter
@NoArgsConstructor
public class ConfiguracionFacturaJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "razon_social", nullable = false, length = 200)
    private String razonSocial;

    @Column(nullable = false, length = 20)
    private String nit;

    @Column(nullable = false, length = 200)
    private String direccion;

    @Column(nullable = false, length = 100)
    private String ciudad;

    @Column(nullable = false, length = 50)
    private String telefono;

    @Column(nullable = false, length = 150)
    private String regimen;

    @Column(name = "resolucion_numero", nullable = false, length = 50)
    private String resolucionNumero;

    @Column(name = "resolucion_fecha")
    private LocalDate resolucionFecha;

    @Column(name = "resolucion_prefijo", nullable = false, length = 10)
    private String resolucionPrefijo;

    @Column(name = "resolucion_desde", nullable = false)
    private int resolucionDesde;

    @Column(name = "resolucion_hasta", nullable = false)
    private int resolucionHasta;

    @Column(name = "consecutivo_actual", nullable = false)
    private int consecutivoActual;
}
