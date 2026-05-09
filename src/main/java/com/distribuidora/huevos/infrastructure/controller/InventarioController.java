package com.distribuidora.huevos.infrastructure.controller;

import com.distribuidora.huevos.application.dto.command.CargarInventarioCommand;
import com.distribuidora.huevos.application.dto.response.InventarioResponse;
import com.distribuidora.huevos.application.service.CargarInventarioService;
import com.distribuidora.huevos.application.service.ConsultarInventarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventario")
public class InventarioController {

    private final ConsultarInventarioService consultarInventarioService;
    private final CargarInventarioService cargarInventarioService;

    public InventarioController(ConsultarInventarioService consultarInventarioService,
                                CargarInventarioService cargarInventarioService) {
        this.consultarInventarioService = consultarInventarioService;
        this.cargarInventarioService = cargarInventarioService;
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
}
