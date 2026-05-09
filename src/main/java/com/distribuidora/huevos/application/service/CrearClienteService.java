package com.distribuidora.huevos.application.service;

import com.distribuidora.huevos.application.dto.command.CrearClienteCommand;
import com.distribuidora.huevos.application.dto.response.ClienteResponse;
import com.distribuidora.huevos.application.mapper.ClienteMapper;
import com.distribuidora.huevos.domain.entities.Cliente;
import com.distribuidora.huevos.domain.enums.TipoCliente;
import com.distribuidora.huevos.domain.exceptions.ClienteIncompletoException;
import com.distribuidora.huevos.domain.repositories.ClienteRepository;
import com.distribuidora.huevos.domain.valueobjects.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CrearClienteService {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;

    public CrearClienteService(ClienteRepository clienteRepository, ClienteMapper clienteMapper) {
        this.clienteRepository = clienteRepository;
        this.clienteMapper = clienteMapper;
    }

    public ClienteResponse ejecutar(CrearClienteCommand command) {
        PrecioEspecial precioEspecial = construirPrecioEspecial(command);
        DescuentoPorVolumen descuentoVolumen = construirDescuentoVolumen(command);

        Cliente cliente = new Cliente(null, command.getNombre(), command.getTipo(),
                precioEspecial, descuentoVolumen);

        Cliente guardado = clienteRepository.save(cliente);
        return clienteMapper.toResponse(guardado);
    }

    private PrecioEspecial construirPrecioEspecial(CrearClienteCommand command) {
        if (command.getTipo() == TipoCliente.ESPECIAL) {
            if (command.getPrecioEspecialExtra() == null || command.getPrecioEspecialNormal() == null) {
                throw new ClienteIncompletoException(
                        "Un cliente ESPECIAL debe tener precioEspecialExtra y precioEspecialNormal definidos");
            }
            return new PrecioEspecial(
                    Precio.de(command.getPrecioEspecialExtra()),
                    Precio.de(command.getPrecioEspecialNormal()));
        }
        return null;
    }

    private DescuentoPorVolumen construirDescuentoVolumen(CrearClienteCommand command) {
        if (command.getDescuentoDesdeCanastas() != null
                && command.getDescuentoPrecioExtra() != null
                && command.getDescuentoPrecioNormal() != null) {
            return new DescuentoPorVolumen(
                    new Cantidad(command.getDescuentoDesdeCanastas()),
                    Precio.de(command.getDescuentoPrecioExtra()),
                    Precio.de(command.getDescuentoPrecioNormal()));
        }
        return null;
    }
}
