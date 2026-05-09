package com.distribuidora.huevos.application.service;

import com.distribuidora.huevos.application.dto.response.PrecioPublicoResponse;
import com.distribuidora.huevos.domain.repositories.PrecioPublicoRepository;
import com.distribuidora.huevos.domain.valueobjects.PrecioPublico;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ConsultarPrecioPublicoService {

    private final PrecioPublicoRepository precioPublicoRepository;

    public ConsultarPrecioPublicoService(PrecioPublicoRepository precioPublicoRepository) {
        this.precioPublicoRepository = precioPublicoRepository;
    }

    public PrecioPublicoResponse ejecutar() {
        PrecioPublico pp = precioPublicoRepository.findCurrent();
        return new PrecioPublicoResponse(pp.getPrecioExtra().getValor(), pp.getPrecioNormal().getValor());
    }
}
