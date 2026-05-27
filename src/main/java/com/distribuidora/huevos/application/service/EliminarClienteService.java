package com.distribuidora.huevos.application.service;

import com.distribuidora.huevos.domain.entities.Cliente;
import com.distribuidora.huevos.domain.exceptions.RecursoNoEncontradoException;
import com.distribuidora.huevos.domain.repositories.AbonoRepository;
import com.distribuidora.huevos.domain.repositories.CargaSaldoRepository;
import com.distribuidora.huevos.domain.repositories.ClienteRepository;
import com.distribuidora.huevos.domain.repositories.CreditoRepository;
import com.distribuidora.huevos.domain.repositories.FacturaRepository;
import com.distribuidora.huevos.domain.repositories.VentaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EliminarClienteService {

    private final ClienteRepository    clienteRepository;
    private final FacturaRepository    facturaRepository;
    private final VentaRepository      ventaRepository;
    private final AbonoRepository      abonoRepository;
    private final CargaSaldoRepository cargaSaldoRepository;
    private final CreditoRepository    creditoRepository;

    public EliminarClienteService(ClienteRepository clienteRepository,
                                  FacturaRepository facturaRepository,
                                  VentaRepository ventaRepository,
                                  AbonoRepository abonoRepository,
                                  CargaSaldoRepository cargaSaldoRepository,
                                  CreditoRepository creditoRepository) {
        this.clienteRepository    = clienteRepository;
        this.facturaRepository    = facturaRepository;
        this.ventaRepository      = ventaRepository;
        this.abonoRepository      = abonoRepository;
        this.cargaSaldoRepository = cargaSaldoRepository;
        this.creditoRepository    = creditoRepository;
    }

    public void ejecutar(Long id) {
        clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Cliente no encontrado con ID: " + id));

        // Eliminar en orden respetando FKs:
        // facturas → ventas → abonos → carga_saldo → crédito → cliente
        facturaRepository.deleteByClienteId(id);
        ventaRepository.deleteByClienteId(id);
        abonoRepository.deleteByClienteId(id);
        cargaSaldoRepository.deleteByClienteId(id);
        creditoRepository.deleteByClienteId(id);
        clienteRepository.deleteById(id);
    }
}
