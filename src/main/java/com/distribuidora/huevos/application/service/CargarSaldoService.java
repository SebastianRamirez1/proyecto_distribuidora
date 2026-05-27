package com.distribuidora.huevos.application.service;

import com.distribuidora.huevos.application.dto.command.CargarSaldoCommand;
import com.distribuidora.huevos.domain.entities.CargaSaldo;
import com.distribuidora.huevos.domain.entities.Cliente;
import com.distribuidora.huevos.domain.entities.Credito;
import com.distribuidora.huevos.domain.exceptions.RecursoNoEncontradoException;
import com.distribuidora.huevos.domain.repositories.CargaSaldoRepository;
import com.distribuidora.huevos.domain.repositories.ClienteRepository;
import com.distribuidora.huevos.domain.repositories.CreditoRepository;
import com.distribuidora.huevos.domain.valueobjects.Dinero;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Carga un saldo deudor directamente al crédito de un cliente,
 * sin crear una venta. Diseñado para migrar deudas registradas
 * en cuadernos físicos al sistema digital.
 */
@Service
@Transactional
public class CargarSaldoService {

    private final ClienteRepository     clienteRepository;
    private final CreditoRepository     creditoRepository;
    private final CargaSaldoRepository  cargaSaldoRepository;

    public CargarSaldoService(ClienteRepository clienteRepository,
                               CreditoRepository creditoRepository,
                               CargaSaldoRepository cargaSaldoRepository) {
        this.clienteRepository   = clienteRepository;
        this.creditoRepository   = creditoRepository;
        this.cargaSaldoRepository = cargaSaldoRepository;
    }

    public void ejecutar(Long clienteId, CargarSaldoCommand command) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Cliente no encontrado con ID: " + clienteId));

        Dinero monto = Dinero.de(command.getMonto());

        // Busca el crédito existente o crea uno nuevo con saldo cero
        Credito credito = creditoRepository.findByClienteId(clienteId)
                .orElseGet(() -> Credito.nuevo(cliente, Dinero.cero()));
        credito.agregarDeuda(monto);
        creditoRepository.save(credito);

        // Registra la entrada de migración para el estado de cuenta
        String descripcion = (command.getDescripcion() != null && !command.getDescripcion().isBlank())
                ? command.getDescripcion().trim()
                : "Saldo migrado de cuaderno";
        cargaSaldoRepository.save(CargaSaldo.nuevo(clienteId, monto, descripcion));
    }
}
