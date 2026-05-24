package com.distribuidora.huevos.application.service;

import com.distribuidora.huevos.application.dto.response.CreditoResponse;
import com.distribuidora.huevos.domain.repositories.CreditoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ConsultarDeudoresService {

    private final CreditoRepository creditoRepository;

    public ConsultarDeudoresService(CreditoRepository creditoRepository) {
        this.creditoRepository = creditoRepository;
    }

    public List<CreditoResponse> ejecutar() {
        return creditoRepository.findDeudores().stream()
                .map(credito -> {
                    CreditoResponse r = new CreditoResponse();
                    r.setClienteId(credito.getCliente().getId());
                    r.setNombreCliente(credito.getCliente().getNombre());
                    r.setMontoTotal(credito.getMontoTotal().getValor());
                    r.setMontoPagado(credito.getMontoPagado().getValor());
                    r.setSaldoPendiente(credito.saldoPendiente().getValor());
                    return r;
                })
                .toList();
    }
}
