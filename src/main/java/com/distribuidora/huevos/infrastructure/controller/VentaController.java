package com.distribuidora.huevos.infrastructure.controller;

import com.distribuidora.huevos.application.dto.command.RegistrarAbonoCommand;
import com.distribuidora.huevos.application.dto.command.RegistrarVentaCommand;
import com.distribuidora.huevos.application.dto.response.VentaResponse;
import com.distribuidora.huevos.application.service.ConsultarVentasDiaService;
import com.distribuidora.huevos.application.service.RegistrarAbonoService;
import com.distribuidora.huevos.application.service.RegistrarVentaService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/ventas")
public class VentaController {

    private final RegistrarVentaService registrarVentaService;
    private final RegistrarAbonoService registrarAbonoService;
    private final ConsultarVentasDiaService consultarVentasDiaService;

    public VentaController(RegistrarVentaService registrarVentaService,
                           RegistrarAbonoService registrarAbonoService,
                           ConsultarVentasDiaService consultarVentasDiaService) {
        this.registrarVentaService = registrarVentaService;
        this.registrarAbonoService = registrarAbonoService;
        this.consultarVentasDiaService = consultarVentasDiaService;
    }

    @GetMapping("/hoy")
    public ResponseEntity<List<VentaResponse>> ventasHoy() {
        return ResponseEntity.ok(consultarVentasDiaService.ejecutar(LocalDate.now()));
    }

    @GetMapping
    public ResponseEntity<List<VentaResponse>> ventasPorFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(consultarVentasDiaService.ejecutar(fecha));
    }

    @PostMapping
    public ResponseEntity<VentaResponse> registrarVenta(
            @Valid @RequestBody RegistrarVentaCommand command) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(registrarVentaService.ejecutar(command));
    }

    @PostMapping("/abono")
    public ResponseEntity<Void> registrarAbono(
            @Valid @RequestBody RegistrarAbonoCommand command) {
        registrarAbonoService.ejecutar(command);
        return ResponseEntity.ok().build();
    }
}
