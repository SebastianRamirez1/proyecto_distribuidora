package com.distribuidora.huevos.infrastructure.controller;

import com.distribuidora.huevos.application.dto.command.ActualizarClienteCommand;
import com.distribuidora.huevos.application.dto.command.ActualizarPrecioCommand;
import com.distribuidora.huevos.application.dto.command.CargarSaldoCommand;
import com.distribuidora.huevos.application.dto.command.CrearClienteCommand;
import com.distribuidora.huevos.application.dto.response.ClienteResponse;
import com.distribuidora.huevos.application.dto.response.MovimientoCuentaResponse;
import com.distribuidora.huevos.application.mapper.ClienteMapper;
import com.distribuidora.huevos.application.service.ActualizarClienteService;
import com.distribuidora.huevos.application.service.ActualizarPrecioClienteService;
import com.distribuidora.huevos.application.service.CargarSaldoService;
import com.distribuidora.huevos.application.service.CrearClienteService;
import com.distribuidora.huevos.application.service.EliminarClienteService;
import com.distribuidora.huevos.application.service.ObtenerEstadoCuentaService;
import com.distribuidora.huevos.domain.repositories.ClienteRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final CrearClienteService            crearClienteService;
    private final ActualizarClienteService       actualizarClienteService;
    private final ActualizarPrecioClienteService actualizarPrecioClienteService;
    private final EliminarClienteService         eliminarClienteService;
    private final CargarSaldoService             cargarSaldoService;
    private final ObtenerEstadoCuentaService     obtenerEstadoCuentaService;
    private final ClienteRepository              clienteRepository;
    private final ClienteMapper                  clienteMapper;

    public ClienteController(CrearClienteService crearClienteService,
                             ActualizarClienteService actualizarClienteService,
                             ActualizarPrecioClienteService actualizarPrecioClienteService,
                             EliminarClienteService eliminarClienteService,
                             CargarSaldoService cargarSaldoService,
                             ObtenerEstadoCuentaService obtenerEstadoCuentaService,
                             ClienteRepository clienteRepository,
                             ClienteMapper clienteMapper) {
        this.crearClienteService            = crearClienteService;
        this.actualizarClienteService       = actualizarClienteService;
        this.actualizarPrecioClienteService = actualizarPrecioClienteService;
        this.eliminarClienteService         = eliminarClienteService;
        this.cargarSaldoService             = cargarSaldoService;
        this.obtenerEstadoCuentaService     = obtenerEstadoCuentaService;
        this.clienteRepository              = clienteRepository;
        this.clienteMapper                  = clienteMapper;
    }

    @GetMapping
    public ResponseEntity<List<ClienteResponse>> listar() {
        List<ClienteResponse> clientes = clienteRepository.findAll().stream()
                .map(clienteMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(clientes);
    }

    @PostMapping
    public ResponseEntity<ClienteResponse> crear(@Valid @RequestBody CrearClienteCommand command) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(crearClienteService.ejecutar(command));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarClienteCommand command) {
        return ResponseEntity.ok(actualizarClienteService.ejecutar(id, command));
    }

    @PutMapping("/{id}/precio-especial")
    public ResponseEntity<ClienteResponse> actualizarPrecio(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarPrecioCommand command) {
        return ResponseEntity.ok(actualizarPrecioClienteService.ejecutar(id, command));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        eliminarClienteService.ejecutar(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Carga un saldo deudor directamente al crédito del cliente sin crear una venta.
     * Usado para migrar deudas de cuadernos físicos al sistema digital.
     */
    @PostMapping("/{id}/saldo-anterior")
    public ResponseEntity<Void> cargarSaldo(
            @PathVariable Long id,
            @Valid @RequestBody CargarSaldoCommand command) {
        cargarSaldoService.ejecutar(id, command);
        return ResponseEntity.ok().build();
    }

    /**
     * Devuelve el estado de cuenta completo del cliente: cargas manuales,
     * ventas al fiado y abonos, ordenados del más reciente al más antiguo.
     */
    @GetMapping("/{id}/estado-cuenta")
    public ResponseEntity<List<MovimientoCuentaResponse>> estadoCuenta(@PathVariable Long id) {
        return ResponseEntity.ok(obtenerEstadoCuentaService.ejecutar(id));
    }
}
