package com.distribuidora.huevos.application.service;

import com.distribuidora.huevos.application.dto.response.PrecioCostoResponse;
import com.distribuidora.huevos.domain.repositories.PrecioCostoRepository;
import com.distribuidora.huevos.domain.valueobjects.PrecioCosto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ConsultarPrecioCostoService {

    private final PrecioCostoRepository precioCostoRepository;

    public ConsultarPrecioCostoService(PrecioCostoRepository precioCostoRepository) {
        this.precioCostoRepository = precioCostoRepository;
    }

    public PrecioCostoResponse ejecutar() {
        PrecioCosto pc = precioCostoRepository.findCurrent();
        return new PrecioCostoResponse(
                pc.getCostoExtra().getValor(),
                pc.getCostoAA().getValor(),
                pc.getCostoA().getValor(),
                pc.getCostoB().getValor());
    }
}
