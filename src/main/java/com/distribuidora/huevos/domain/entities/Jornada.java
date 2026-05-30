package com.distribuidora.huevos.domain.entities;

import com.distribuidora.huevos.domain.enums.EstadoJornada;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Jornada {

    private Long id;
    private LocalDate fecha;
    private EstadoJornada estado;
    private LocalDateTime abiertaEn;
    private LocalDateTime cerradaEn;

    public Jornada(Long id, LocalDate fecha, EstadoJornada estado,
                   LocalDateTime abiertaEn, LocalDateTime cerradaEn) {
        this.id        = id;
        this.fecha     = fecha;
        this.estado    = estado;
        this.abiertaEn = abiertaEn;
        this.cerradaEn = cerradaEn;
    }

    /** Crea una jornada nueva (ABIERTA) para la fecha indicada. */
    public static Jornada nueva(LocalDate fecha) {
        return new Jornada(null, fecha, EstadoJornada.ABIERTA, LocalDateTime.now(), null);
    }

    /** Cierra la jornada (liquida la hoja). */
    public void cerrar() {
        this.estado    = EstadoJornada.CERRADA;
        this.cerradaEn = LocalDateTime.now();
    }

    public boolean estaAbierta() {
        return EstadoJornada.ABIERTA == this.estado;
    }

    // ── Getters ────────────────────────────────────────────────────────────────

    public Long getId()                  { return id;        }
    public LocalDate getFecha()          { return fecha;     }
    public EstadoJornada getEstado()     { return estado;    }
    public LocalDateTime getAbiertaEn() { return abiertaEn; }
    public LocalDateTime getCerradaEn() { return cerradaEn; }
}
