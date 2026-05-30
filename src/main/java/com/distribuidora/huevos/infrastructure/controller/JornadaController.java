package com.distribuidora.huevos.infrastructure.controller;

import com.distribuidora.huevos.application.dto.response.JornadaResponse;
import com.distribuidora.huevos.application.dto.response.JornadasEstadoResponse;
import com.distribuidora.huevos.application.service.CerrarJornadaService;
import com.distribuidora.huevos.application.service.LiquidarJornadaService;
import com.distribuidora.huevos.application.service.ObtenerJornadaActivaService;
import com.distribuidora.huevos.domain.repositories.JornadaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jornadas")
public class JornadaController {

    private final ObtenerJornadaActivaService obtenerActiva;
    private final LiquidarJornadaService liquidar;
    private final CerrarJornadaService cerrar;
    private final JornadaRepository jornadaRepository;

    public JornadaController(ObtenerJornadaActivaService obtenerActiva,
                              LiquidarJornadaService liquidar,
                              CerrarJornadaService cerrar,
                              JornadaRepository jornadaRepository) {
        this.obtenerActiva    = obtenerActiva;
        this.liquidar         = liquidar;
        this.cerrar           = cerrar;
        this.jornadaRepository = jornadaRepository;
    }

    /**
     * Retorna el estado completo: jornada ABIERTA + jornada EN_CIERRE (si existe).
     * La jornada ABIERTA se crea automáticamente si no existe.
     */
    @GetMapping("/estado")
    public ResponseEntity<JornadasEstadoResponse> getEstado() {
        JornadaResponse abierta  = obtenerActiva.ejecutar();
        JornadaResponse enCierre = jornadaRepository.findEnCierre()
                .map(ObtenerJornadaActivaService::toResponse)
                .orElse(null);
        return ResponseEntity.ok(new JornadasEstadoResponse(abierta, enCierre));
    }

    /** Retorna solo la jornada activa. */
    @GetMapping("/activa")
    public ResponseEntity<JornadaResponse> getActiva() {
        return ResponseEntity.ok(obtenerActiva.ejecutar());
    }

    /** Liquida la jornada activa: pasa a EN_CIERRE y abre la del día siguiente. */
    @PostMapping("/liquidar")
    public ResponseEntity<JornadaResponse> liquidar() {
        return ResponseEntity.ok(liquidar.ejecutar());
    }

    /** Cierra definitivamente la jornada EN_CIERRE. */
    @PostMapping("/cerrar")
    public ResponseEntity<JornadaResponse> cerrar() {
        return ResponseEntity.ok(cerrar.ejecutar());
    }
}
