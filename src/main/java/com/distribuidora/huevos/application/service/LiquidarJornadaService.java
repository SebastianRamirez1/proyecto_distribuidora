package com.distribuidora.huevos.application.service;

import com.distribuidora.huevos.application.dto.response.JornadaResponse;
import com.distribuidora.huevos.domain.entities.Jornada;
import com.distribuidora.huevos.domain.exceptions.OperacionNoPermitidaException;
import com.distribuidora.huevos.domain.repositories.JornadaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LiquidarJornadaService {

    private final JornadaRepository jornadaRepository;

    public LiquidarJornadaService(JornadaRepository jornadaRepository) {
        this.jornadaRepository = jornadaRepository;
    }

    /**
     * Cierra la jornada activa y abre automáticamente la del día siguiente.
     * Retorna la nueva jornada abierta.
     */
    public JornadaResponse ejecutar() {
        Jornada actual = jornadaRepository.findActiva()
                .orElseThrow(() -> new OperacionNoPermitidaException(
                        "No hay ninguna jornada abierta para liquidar."));

        actual.cerrar();
        jornadaRepository.save(actual);

        Jornada siguiente = Jornada.nueva(actual.getFecha().plusDays(1));
        siguiente = jornadaRepository.save(siguiente);

        return ObtenerJornadaActivaService.toResponse(siguiente);
    }
}
