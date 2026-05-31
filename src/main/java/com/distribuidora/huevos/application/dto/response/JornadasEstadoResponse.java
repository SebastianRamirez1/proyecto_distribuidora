package com.distribuidora.huevos.application.dto.response;

/**
 * Estado combinado de las jornadas en curso.
 * abierta  — siempre presente (se crea si no existe).
 * enCierre — presente solo cuando hay una jornada pendiente de cierre definitivo.
 */
public record JornadasEstadoResponse(
        JornadaResponse abierta,
        JornadaResponse enCierre   // null si no hay ninguna
) {}
