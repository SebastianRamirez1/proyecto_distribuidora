package com.distribuidora.huevos.application.service;

import com.distribuidora.huevos.application.dto.command.ActualizarClienteCommand;
import com.distribuidora.huevos.application.dto.response.ClienteResponse;
import com.distribuidora.huevos.application.mapper.ClienteMapper;
import com.distribuidora.huevos.domain.entities.Cliente;
import com.distribuidora.huevos.domain.enums.TipoCliente;
import com.distribuidora.huevos.domain.exceptions.ClienteIncompletoException;
import com.distribuidora.huevos.domain.exceptions.RecursoNoEncontradoException;
import com.distribuidora.huevos.domain.repositories.ClienteRepository;
import com.distribuidora.huevos.domain.valueobjects.Precio;
import com.distribuidora.huevos.domain.valueobjects.PrecioEspecial;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ActualizarClienteService {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;

    public ActualizarClienteService(ClienteRepository clienteRepository,
                                    ClienteMapper clienteMapper) {
        this.clienteRepository = clienteRepository;
        this.clienteMapper = clienteMapper;
    }

    public ClienteResponse ejecutar(Long id, ActualizarClienteCommand command) {
        Cliente existente = clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Cliente no encontrado con ID: " + id));

        PrecioEspecial precioEspecial = null;
        if (command.getTipo() == TipoCliente.ESPECIAL) {
            if (command.getPrecioEspecialExtra() == null
                    || command.getPrecioEspecialAA() == null
                    || command.getPrecioEspecialA()  == null
                    || command.getPrecioEspecialB()  == null) {
                throw new ClienteIncompletoException(
                        "Un cliente ESPECIAL debe tener precios especiales para todos los tipos: EXTRA, AA, A, B");
            }
            precioEspecial = new PrecioEspecial(
                    Precio.de(command.getPrecioEspecialExtra()),
                    Precio.de(command.getPrecioEspecialAA()),
                    Precio.de(command.getPrecioEspecialA()),
                    Precio.de(command.getPrecioEspecialB()));
        }

        // Preserva el descuento por volumen existente; nombre, tipo y notas se actualizan
        Cliente actualizado = new Cliente(existente.getId(), command.getNombre(),
                command.getTipo(), precioEspecial, existente.getDescuentoVolumen(),
                command.getNotas());

        return clienteMapper.toResponse(clienteRepository.save(actualizado));
    }
}
