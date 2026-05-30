package com.distribuidora.huevos.application.service;

import com.distribuidora.huevos.application.dto.response.JornadaResponse;
import com.distribuidora.huevos.domain.entities.Jornada;
import com.distribuidora.huevos.domain.exceptions.OperacionNoPermitidaException;
import com.distribuidora.huevos.domain.repositories.JornadaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CerrarJornadaService {

    private final JornadaRepository jornadaRepository;

    public CerrarJornadaService(JornadaRepository jornadaRepository) {
        this.jornadaRepository = jornadaRepository;
    }

    /**
     * Cierra definitivamente la jornada que está EN_CIERRE.
     * Llamar solo cuando ya no quedan ventas rezagadas de esa hoja.
     */
    public JornadaResponse ejecutar() {
        Jornada enCierre = jornadaRepository.findEnCierre()
                .orElseThrow(() -> new OperacionNoPermitidaException(
                        "No hay ninguna jornada en cierre para cerrar definitivamente."));

        enCierre.cerrar();
        jornadaRepository.save(enCierre);

        return ObtenerJornadaActivaService.toResponse(enCierre);
    }
}
