package com.distribuidora.huevos.application.service;

import com.distribuidora.huevos.domain.entities.Cliente;
import com.distribuidora.huevos.domain.exceptions.RecursoNoEncontradoException;
import com.distribuidora.huevos.domain.repositories.AbonoRepository;
import com.distribuidora.huevos.domain.repositories.ClienteRepository;
import com.distribuidora.huevos.domain.repositories.CreditoRepository;
import com.distribuidora.huevos.domain.repositories.FacturaRepository;
import com.distribuidora.huevos.domain.repositories.VentaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EliminarClienteService {

    private final ClienteRepository clienteRepository;
    private final FacturaRepository facturaRepository;
    private final VentaRepository ventaRepository;
    private final CreditoRepository creditoRepository;
    private final AbonoRepository abonoRepository;

    public EliminarClienteService(ClienteRepository clienteRepository,
                                  FacturaRepository facturaRepository,
                                  VentaRepository ventaRepository,
                                  CreditoRepository creditoRepository,
                                  AbonoRepository abonoRepository) {
        this.clienteRepository = clienteRepository;
        this.facturaRepository = facturaRepository;
        this.ventaRepository   = ventaRepository;
        this.creditoRepository = creditoRepository;
        this.abonoRepository   = abonoRepository;
    }

    public void ejecutar(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Cliente no encontrado con ID: " + id));

        // Eliminar en orden respetando FKs: facturas → ventas → abonos → crédito → cliente
        facturaRepository.deleteByClienteId(id);
        ventaRepository.deleteByClienteId(id);
        abonoRepository.deleteByClienteId(id);
        creditoRepository.deleteByClienteId(id);
        clienteRepository.deleteById(id);
    }
}
