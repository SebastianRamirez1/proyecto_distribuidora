package com.distribuidora.huevos.infrastructure.controller;

import com.distribuidora.huevos.application.dto.response.AbonoResponse;
import com.distribuidora.huevos.application.dto.response.CreditoResponse;
import com.distribuidora.huevos.application.service.ConsultarCreditoService;
import com.distribuidora.huevos.application.service.ConsultarDeudoresService;
import com.distribuidora.huevos.application.service.ConsultarHistorialAbonosService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/creditos")
public class CreditoController {

    private final ConsultarCreditoService consultarCreditoService;
    private final ConsultarDeudoresService consultarDeudoresService;
    private final ConsultarHistorialAbonosService consultarHistorialAbonosService;

    public CreditoController(ConsultarCreditoService consultarCreditoService,
                             ConsultarDeudoresService consultarDeudoresService,
                             ConsultarHistorialAbonosService consultarHistorialAbonosService) {
        this.consultarCreditoService        = consultarCreditoService;
        this.consultarDeudoresService       = consultarDeudoresService;
        this.consultarHistorialAbonosService = consultarHistorialAbonosService;
    }

    @GetMapping("/deudores")
    public ResponseEntity<List<CreditoResponse>> obtenerDeudores() {
        return ResponseEntity.ok(consultarDeudoresService.ejecutar());
    }

    /** Historial de abonos de un cliente, del más reciente al más antiguo. */
    @GetMapping("/{clienteId}/abonos")
    public ResponseEntity<List<AbonoResponse>> obtenerHistorial(@PathVariable Long clienteId) {
        return ResponseEntity.ok(consultarHistorialAbonosService.ejecutar(clienteId));
    }

    @GetMapping("/{clienteId}")
    public ResponseEntity<CreditoResponse> obtenerCredito(@PathVariable Long clienteId) {
        return ResponseEntity.ok(consultarCreditoService.ejecutar(clienteId));
    }
}
