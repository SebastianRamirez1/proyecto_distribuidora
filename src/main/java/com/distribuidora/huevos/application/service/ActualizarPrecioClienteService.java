package com.distribuidora.huevos.application.service;

import com.distribuidora.huevos.application.dto.command.ActualizarPrecioCommand;
import com.distribuidora.huevos.application.dto.response.ClienteResponse;
import com.distribuidora.huevos.application.mapper.ClienteMapper;
import com.distribuidora.huevos.domain.entities.Cliente;
import com.distribuidora.huevos.domain.enums.TipoCliente;
import com.distribuidora.huevos.domain.exceptions.ClienteIncompletoException;
import com.distribuidora.huevos.domain.exceptions.RecursoNoEncontradoException;
import com.distribuidora.huevos.domain.repositories.ClienteRepository;
import com.distribuidora.huevos.domain.valueobjects.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ActualizarPrecioClienteService {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;

    public ActualizarPrecioClienteService(ClienteRepository clienteRepository,
                                          ClienteMapper clienteMapper) {
        this.clienteRepository = clienteRepository;
        this.clienteMapper = clienteMapper;
    }

    public ClienteResponse ejecutar(Long clienteId, ActualizarPrecioCommand command) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Cliente no encontrado con ID: " + clienteId));

        if (cliente.getTipo() != TipoCliente.ESPECIAL) {
            throw new ClienteIncompletoException(
                    "Solo se puede actualizar precio especial a clientes de tipo ESPECIAL. " +
                    "Cliente '" + cliente.getNombre() + "' es de tipo " + cliente.getTipo());
        }

        PrecioEspecial nuevoPrecio = new PrecioEspecial(
                Precio.de(command.getPrecioEspecialExtra()),
                Precio.de(command.getPrecioEspecialNormal()));

        Cliente actualizado = cliente.conPrecioEspecial(nuevoPrecio);

        if (command.getDescuentoDesdeCanastas() != null
                && command.getDescuentoPrecioExtra() != null
                && command.getDescuentoPrecioNormal() != null) {
            DescuentoPorVolumen descuento = new DescuentoPorVolumen(
                    new Cantidad(command.getDescuentoDesdeCanastas()),
                    Precio.de(command.getDescuentoPrecioExtra()),
                    Precio.de(command.getDescuentoPrecioNormal()));
            actualizado = actualizado.conDescuentoVolumen(descuento);
        }

        Cliente guardado = clienteRepository.save(actualizado);
        return clienteMapper.toResponse(guardado);
    }
}
