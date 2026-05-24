package com.distribuidora.huevos.application.service;

import com.distribuidora.huevos.application.dto.response.AbonoResponse;
import com.distribuidora.huevos.domain.repositories.AbonoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ConsultarHistorialAbonosService {

    private final AbonoRepository abonoRepository;

    public ConsultarHistorialAbonosService(AbonoRepository abonoRepository) {
        this.abonoRepository = abonoRepository;
    }

    public List<AbonoResponse> ejecutar(Long clienteId) {
        return abonoRepository.findByClienteIdOrderByFechaDesc(clienteId)
                .stream()
                .map(a -> {
                    AbonoResponse r = new AbonoResponse();
                    r.setId(a.getId());
                    r.setMonto(a.getMonto().getValor());
                    r.setMedioPago(a.getMedioPago().name());
                    r.setFecha(a.getFecha());
                    return r;
                })
                .toList();
    }
}
