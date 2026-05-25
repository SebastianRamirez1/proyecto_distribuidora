package com.distribuidora.huevos.infrastructure.controller;

import com.distribuidora.huevos.application.dto.command.ActualizarPrecioCostoCommand;
import com.distribuidora.huevos.application.dto.command.ActualizarPrecioPublicoCommand;
import com.distribuidora.huevos.application.dto.response.PrecioCostoResponse;
import com.distribuidora.huevos.application.dto.response.PrecioPublicoResponse;
import com.distribuidora.huevos.application.service.ActualizarPrecioCostoService;
import com.distribuidora.huevos.application.service.ActualizarPrecioPublicoService;
import com.distribuidora.huevos.application.service.ConsultarPrecioCostoService;
import com.distribuidora.huevos.application.service.ConsultarPrecioPublicoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/precios")
public class PrecioController {

    private final ActualizarPrecioPublicoService actualizarPrecioPublicoService;
    private final ConsultarPrecioPublicoService consultarPrecioPublicoService;
    private final ActualizarPrecioCostoService actualizarPrecioCostoService;
    private final ConsultarPrecioCostoService consultarPrecioCostoService;

    public PrecioController(ActualizarPrecioPublicoService actualizarPrecioPublicoService,
                            ConsultarPrecioPublicoService consultarPrecioPublicoService,
                            ActualizarPrecioCostoService actualizarPrecioCostoService,
                            ConsultarPrecioCostoService consultarPrecioCostoService) {
        this.actualizarPrecioPublicoService = actualizarPrecioPublicoService;
        this.consultarPrecioPublicoService = consultarPrecioPublicoService;
        this.actualizarPrecioCostoService = actualizarPrecioCostoService;
        this.consultarPrecioCostoService = consultarPrecioCostoService;
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

    @GetMapping("/costo")
    public ResponseEntity<PrecioCostoResponse> obtenerPrecioCosto() {
        return ResponseEntity.ok(consultarPrecioCostoService.ejecutar());
    }

    @PutMapping("/costo")
    public ResponseEntity<PrecioCostoResponse> actualizarPrecioCosto(
            @Valid @RequestBody ActualizarPrecioCostoCommand command) {
        return ResponseEntity.ok(actualizarPrecioCostoService.ejecutar(command));
    }
}
