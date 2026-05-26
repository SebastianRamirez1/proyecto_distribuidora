package com.distribuidora.huevos.application.service;

import com.distribuidora.huevos.domain.entities.Cliente;
import com.distribuidora.huevos.domain.exceptions.OperacionNoPermitidaException;
import com.distribuidora.huevos.domain.exceptions.RecursoNoEncontradoException;
import com.distribuidora.huevos.domain.repositories.AbonoRepository;
import com.distribuidora.huevos.domain.repositories.ClienteRepository;
import com.distribuidora.huevos.domain.repositories.CreditoRepository;
import com.distribuidora.huevos.domain.repositories.VentaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EliminarClienteService {

    private final ClienteRepository clienteRepository;
    private final VentaRepository ventaRepository;
    private final CreditoRepository creditoRepository;
    private final AbonoRepository abonoRepository;

    public EliminarClienteService(ClienteRepository clienteRepository,
                                  VentaRepository ventaRepository,
                                  CreditoRepository creditoRepository,
                                  AbonoRepository abonoRepository) {
        this.clienteRepository = clienteRepository;
        this.ventaRepository   = ventaRepository;
        this.creditoRepository = creditoRepository;
        this.abonoRepository   = abonoRepository;
    }

    public void ejecutar(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Cliente no encontrado con ID: " + id));

        if ("Público General".equals(cliente.getNombre())) {
            throw new OperacionNoPermitidaException(
                    "El cliente 'Público General' no puede ser eliminado.");
        }

        if (ventaRepository.existsByClienteId(id)) {
            throw new OperacionNoPermitidaException(
                    "No se puede eliminar el cliente porque tiene ventas registradas. " +
                    "Eliminar clientes con historial de ventas comprometería la integridad financiera.");
        }

        abonoRepository.deleteByClienteId(id);
        creditoRepository.deleteByClienteId(id);
        clienteRepository.deleteById(id);
    }
}
