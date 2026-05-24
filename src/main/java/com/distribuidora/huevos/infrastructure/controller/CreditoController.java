package com.distribuidora.huevos.infrastructure.controller;

import com.distribuidora.huevos.application.dto.response.CreditoResponse;
import com.distribuidora.huevos.application.service.ConsultarCreditoService;
import com.distribuidora.huevos.application.service.ConsultarDeudoresService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/creditos")
public class CreditoController {

    private final ConsultarCreditoService consultarCreditoService;
    private final ConsultarDeudoresService consultarDeudoresService;

    public CreditoController(ConsultarCreditoService consultarCreditoService,
                             ConsultarDeudoresService consultarDeudoresService) {
        this.consultarCreditoService = consultarCreditoService;
        this.consultarDeudoresService = consultarDeudoresService;
    }

    @GetMapping("/deudores")
    public ResponseEntity<List<CreditoResponse>> obtenerDeudores() {
        return ResponseEntity.ok(consultarDeudoresService.ejecutar());
    }

    @GetMapping("/{clienteId}")
    public ResponseEntity<CreditoResponse> obtenerCredito(@PathVariable Long clienteId) {
        return ResponseEntity.ok(consultarCreditoService.ejecutar(clienteId));
    }
}
