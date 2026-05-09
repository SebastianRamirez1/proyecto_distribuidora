package com.distribuidora.huevos.infrastructure.controller;

import com.distribuidora.huevos.application.dto.command.ActualizarPrecioCommand;
import com.distribuidora.huevos.application.dto.command.CrearClienteCommand;
import com.distribuidora.huevos.application.dto.response.ClienteResponse;
import com.distribuidora.huevos.application.mapper.ClienteMapper;
import com.distribuidora.huevos.application.service.ActualizarPrecioClienteService;
import com.distribuidora.huevos.application.service.CrearClienteService;
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

    private final CrearClienteService crearClienteService;
    private final ActualizarPrecioClienteService actualizarPrecioClienteService;
    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;

    public ClienteController(CrearClienteService crearClienteService,
                             ActualizarPrecioClienteService actualizarPrecioClienteService,
                             ClienteRepository clienteRepository,
                             ClienteMapper clienteMapper) {
        this.crearClienteService = crearClienteService;
        this.actualizarPrecioClienteService = actualizarPrecioClienteService;
        this.clienteRepository = clienteRepository;
        this.clienteMapper = clienteMapper;
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

    @PutMapping("/{id}/precio-especial")
    public ResponseEntity<ClienteResponse> actualizarPrecio(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarPrecioCommand command) {
        return ResponseEntity.ok(actualizarPrecioClienteService.ejecutar(id, command));
    }
}
