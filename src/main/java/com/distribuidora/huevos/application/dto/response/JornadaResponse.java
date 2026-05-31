package com.distribuidora.huevos.application.dto.response;

import com.distribuidora.huevos.domain.enums.EstadoJornada;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record JornadaResponse(
        Long id,
        LocalDate fecha,
        EstadoJornada estado,
        LocalDateTime abiertaEn,
        LocalDateTime cerradaEn
) {}
