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
            if (command.getPrecioEspecialExtra() == null
                    || command.getPrecioEspecialAA() == null
                    || command.getPrecioEspecialA()  == null
                    || command.getPrecioEspecialB()  == null) {
                throw new ClienteIncompletoException(
                        "Un cliente ESPECIAL debe tener precios especiales definidos para todos los tipos: EXTRA, AA, A, B");
            }
            return new PrecioEspecial(
                    Precio.de(command.getPrecioEspecialExtra()),
                    Precio.de(command.getPrecioEspecialAA()),
                    Precio.de(command.getPrecioEspecialA()),
                    Precio.de(command.getPrecioEspecialB()));
        }
        return null;
    }

    private DescuentoPorVolumen construirDescuentoVolumen(CrearClienteCommand command) {
        if (command.getDescuentoDesdeCanastas() != null
                && command.getDescuentoPrecioExtra() != null
                && command.getDescuentoPrecioAA()    != null
                && command.getDescuentoPrecioA()     != null
                && command.getDescuentoPrecioB()     != null) {
            return new DescuentoPorVolumen(
                    new Cantidad(command.getDescuentoDesdeCanastas()),
                    Precio.de(command.getDescuentoPrecioExtra()),
                    Precio.de(command.getDescuentoPrecioAA()),
                    Precio.de(command.getDescuentoPrecioA()),
                    Precio.de(command.getDescuentoPrecioB()));
        }
        return null;
    }
}
