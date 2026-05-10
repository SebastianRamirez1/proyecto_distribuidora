package com.distribuidora.huevos.application.service;

import com.distribuidora.huevos.application.dto.response.CreditoResponse;
import com.distribuidora.huevos.domain.entities.Credito;
import com.distribuidora.huevos.domain.exceptions.RecursoNoEncontradoException;
import com.distribuidora.huevos.domain.repositories.CreditoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ConsultarCreditoService {

    private final CreditoRepository creditoRepository;

    public ConsultarCreditoService(CreditoRepository creditoRepository) {
        this.creditoRepository = creditoRepository;
    }

    public CreditoResponse ejecutar(Long clienteId) {
        Credito credito = creditoRepository.findByClienteId(clienteId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe crédito para el cliente con ID: " + clienteId));

        CreditoResponse response = new CreditoResponse();
        response.setClienteId(credito.getCliente().getId());
        response.setNombreCliente(credito.getCliente().getNombre());
        response.setMontoTotal(credito.getMontoTotal().getValor());
        response.setMontoPagado(credito.getMontoPagado().getValor());
        response.setSaldoPendiente(credito.saldoPendiente().getValor());
        return response;
    }
}
