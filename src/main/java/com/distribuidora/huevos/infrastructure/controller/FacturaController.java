package com.distribuidora.huevos.infrastructure.controller;

import com.distribuidora.huevos.application.dto.command.ConfigurarFacturaCommand;
import com.distribuidora.huevos.application.dto.command.GenerarFacturaCommand;
import com.distribuidora.huevos.application.dto.response.ConfiguracionFacturaResponse;
import com.distribuidora.huevos.application.dto.response.FacturaResponse;
import com.distribuidora.huevos.application.service.*;
import com.distribuidora.huevos.domain.entities.ConfiguracionFactura;
import com.distribuidora.huevos.domain.entities.Factura;
import com.distribuidora.huevos.domain.exceptions.RecursoNoEncontradoException;
import com.distribuidora.huevos.domain.repositories.ConfiguracionFacturaRepository;
import com.distribuidora.huevos.domain.repositories.FacturaRepository;
import com.distribuidora.huevos.infrastructure.pdf.FacturaPdfGenerator;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/facturas")
public class FacturaController {

    private final GenerarFacturaService generarService;
    private final ListarFacturasService listarService;
    private final ObtenerConfiguracionFacturaService obtenerConfigService;
    private final ActualizarConfiguracionFacturaService actualizarConfigService;
    private final FacturaRepository facturaRepository;
    private final ConfiguracionFacturaRepository configRepo;
    private final FacturaPdfGenerator pdfGenerator;

    public FacturaController(GenerarFacturaService generarService,
                              ListarFacturasService listarService,
                              ObtenerConfiguracionFacturaService obtenerConfigService,
                              ActualizarConfiguracionFacturaService actualizarConfigService,
                              FacturaRepository facturaRepository,
                              ConfiguracionFacturaRepository configRepo,
                              FacturaPdfGenerator pdfGenerator) {
        this.generarService = generarService;
        this.listarService = listarService;
        this.obtenerConfigService = obtenerConfigService;
        this.actualizarConfigService = actualizarConfigService;
        this.facturaRepository = facturaRepository;
        this.configRepo = configRepo;
        this.pdfGenerator = pdfGenerator;
    }

    // ── Configuración ─────────────────────────────────────────────────────────

    @GetMapping("/configuracion")
    public ConfiguracionFacturaResponse obtenerConfiguracion() {
        return obtenerConfigService.ejecutar();
    }

    @PutMapping("/configuracion")
    public ConfiguracionFacturaResponse actualizarConfiguracion(
            @Valid @RequestBody ConfigurarFacturaCommand cmd) {
        return actualizarConfigService.ejecutar(cmd);
    }

    // ── Facturas ──────────────────────────────────────────────────────────────

    @GetMapping
    public List<FacturaResponse> listar() {
        return listarService.ejecutar();
    }

    @PostMapping("/generar")
    @ResponseStatus(HttpStatus.CREATED)
    public FacturaResponse generar(@Valid @RequestBody GenerarFacturaCommand cmd) {
        return generarService.ejecutar(cmd);
    }

    // ── Descarga PDF ──────────────────────────────────────────────────────────

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> descargarPdf(@PathVariable Long id) {
        Factura factura = facturaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Factura no encontrada con ID: " + id));

        ConfiguracionFactura cfg = configRepo.findUnica();
        byte[] pdf = pdfGenerator.generar(factura, cfg);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment",
                "factura-" + factura.getNumero() + ".pdf");
        headers.setContentLength(pdf.length);

        return ResponseEntity.ok().headers(headers).body(pdf);
    }
}
