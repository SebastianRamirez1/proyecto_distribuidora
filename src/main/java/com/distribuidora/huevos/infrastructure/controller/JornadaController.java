package com.distribuidora.huevos.infrastructure.controller;

import com.distribuidora.huevos.application.dto.response.JornadaResponse;
import com.distribuidora.huevos.application.service.LiquidarJornadaService;
import com.distribuidora.huevos.application.service.ObtenerJornadaActivaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jornadas")
public class JornadaController {

    private final ObtenerJornadaActivaService obtenerActiva;
    private final LiquidarJornadaService liquidar;

    public JornadaController(ObtenerJornadaActivaService obtenerActiva,
                              LiquidarJornadaService liquidar) {
        this.obtenerActiva = obtenerActiva;
        this.liquidar      = liquidar;
    }

    /** Retorna la jornada activa. Si no existe ninguna, crea la de hoy. */
    @GetMapping("/activa")
    public ResponseEntity<JornadaResponse> getActiva() {
        return ResponseEntity.ok(obtenerActiva.ejecutar());
    }

    /** Liquida la jornada activa y abre automáticamente la del día siguiente. */
    @PostMapping("/liquidar")
    public ResponseEntity<JornadaResponse> liquidar() {
        return ResponseEntity.ok(liquidar.ejecutar());
    }
}
