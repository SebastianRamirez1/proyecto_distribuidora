package com.distribuidora.huevos.infrastructure.controller;

import com.distribuidora.huevos.application.dto.response.ReporteCajaResponse;
import com.distribuidora.huevos.application.service.GenerarReporteCajaService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    private final GenerarReporteCajaService generarReporteCajaService;

    public ReporteController(GenerarReporteCajaService generarReporteCajaService) {
        this.generarReporteCajaService = generarReporteCajaService;
    }

    @GetMapping("/caja/hoy")
    public ResponseEntity<ReporteCajaResponse> cajaHoy() {
        return ResponseEntity.ok(generarReporteCajaService.ejecutar(LocalDate.now()));
    }

    @GetMapping("/caja")
    public ResponseEntity<ReporteCajaResponse> cajaPorFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(generarReporteCajaService.ejecutar(fecha));
    }
}
