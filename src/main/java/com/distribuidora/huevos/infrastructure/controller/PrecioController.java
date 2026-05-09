package com.distribuidora.huevos.infrastructure.controller;

import com.distribuidora.huevos.application.dto.command.ActualizarPrecioPublicoCommand;
import com.distribuidora.huevos.application.dto.response.PrecioPublicoResponse;
import com.distribuidora.huevos.application.service.ActualizarPrecioPublicoService;
import com.distribuidora.huevos.application.service.ConsultarPrecioPublicoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/precios")
public class PrecioController {

    private final ActualizarPrecioPublicoService actualizarPrecioPublicoService;
    private final ConsultarPrecioPublicoService consultarPrecioPublicoService;

    public PrecioController(ActualizarPrecioPublicoService actualizarPrecioPublicoService,
                            ConsultarPrecioPublicoService consultarPrecioPublicoService) {
        this.actualizarPrecioPublicoService = actualizarPrecioPublicoService;
        this.consultarPrecioPublicoService = consultarPrecioPublicoService;
    }

    @GetMapping("/publico")
    public ResponseEntity<PrecioPublicoResponse> obtenerPrecioPublico() {
        return ResponseEntity.ok(consultarPrecioPublicoService.ejecutar());
    }

    @PutMapping("/publico")
    public ResponseEntity<PrecioPublicoResponse> actualizarPrecioPublico(
            @Valid @RequestBody ActualizarPrecioPublicoCommand command) {
        return ResponseEntity.ok(actualizarPrecioPublicoService.ejecutar(command));
    }
}
