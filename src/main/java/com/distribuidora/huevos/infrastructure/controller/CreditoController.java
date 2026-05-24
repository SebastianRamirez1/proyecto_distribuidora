package com.distribuidora.huevos.infrastructure.controller;

import com.distribuidora.huevos.application.dto.response.CreditoResponse;
import com.distribuidora.huevos.application.service.ConsultarCreditoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/creditos")
public class CreditoController {

    private final ConsultarCreditoService consultarCreditoService;

    public CreditoController(ConsultarCreditoService consultarCreditoService) {
        this.consultarCreditoService = consultarCreditoService;
    }

    @GetMapping("/{clienteId}")
    public ResponseEntity<CreditoResponse> obtenerCredito(@PathVariable Long clienteId) {
        return ResponseEntity.ok(consultarCreditoService.ejecutar(clienteId));
    }
}
