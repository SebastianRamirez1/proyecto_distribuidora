package com.distribuidora.huevos.infrastructure.controller;

import com.distribuidora.huevos.application.dto.command.CargarInventarioCommand;
import com.distribuidora.huevos.application.dto.command.CargarInventarioBulkCommand;
import com.distribuidora.huevos.application.dto.response.InventarioResponse;
import com.distribuidora.huevos.application.service.CargarInventarioService;
import com.distribuidora.huevos.application.service.CargarInventarioBulkService;
import com.distribuidora.huevos.application.service.ConsultarInventarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventario")
public class InventarioController {

    private final ConsultarInventarioService consultarInventarioService;
    private final CargarInventarioService cargarInventarioService;
    private final CargarInventarioBulkService cargarInventarioBulkService;

    public InventarioController(ConsultarInventarioService consultarInventarioService,
                                CargarInventarioService cargarInventarioService,
                                CargarInventarioBulkService cargarInventarioBulkService) {
        this.consultarInventarioService = consultarInventarioService;
        this.cargarInventarioService = cargarInventarioService;
        this.cargarInventarioBulkService = cargarInventarioBulkService;
    }

    @GetMapping
    public ResponseEntity<InventarioResponse> consultar() {
        return ResponseEntity.ok(consultarInventarioService.ejecutar());
    }

    @PostMapping("/cargar")
    public ResponseEntity<InventarioResponse> cargar(
            @Valid @RequestBody CargarInventarioCommand command) {
        return ResponseEntity.ok(cargarInventarioService.ejecutar(command));
    }

    /** Carga los 4 tipos en una sola transacción atómica. */
    @PostMapping("/cargar-bulk")
    public ResponseEntity<InventarioResponse> cargarBulk(
            @RequestBody CargarInventarioBulkCommand command) {
        return ResponseEntity.ok(cargarInventarioBulkService.ejecutar(command));
    }
}
