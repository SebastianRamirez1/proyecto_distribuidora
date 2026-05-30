package com.distribuidora.huevos.application.service;

import com.distribuidora.huevos.application.dto.response.JornadaResponse;
import com.distribuidora.huevos.domain.entities.Jornada;
import com.distribuidora.huevos.domain.repositories.JornadaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@Transactional
public class ObtenerJornadaActivaService {

    private final JornadaRepository jornadaRepository;

    public ObtenerJornadaActivaService(JornadaRepository jornadaRepository) {
        this.jornadaRepository = jornadaRepository;
    }

    public JornadaResponse ejecutar() {
        Jornada jornada = jornadaRepository.findActiva()
                .orElseGet(() -> {
                    // Primera vez o después de un reset: crear la jornada de hoy
                    Jornada nueva = Jornada.nueva(LocalDate.now());
                    return jornadaRepository.save(nueva);
                });
        return toResponse(jornada);
    }

    /** Reutilizable desde otros servicios. */
    public static JornadaResponse toResponse(Jornada j) {
        return new JornadaResponse(
                j.getId(), j.getFecha(), j.getEstado(),
                j.getAbiertaEn(), j.getCerradaEn()
        );
    }
}
