package com.distribuidora.huevos.application.service;

import com.distribuidora.huevos.application.dto.command.ActualizarPrecioCostoCommand;
import com.distribuidora.huevos.application.dto.response.PrecioCostoResponse;
import com.distribuidora.huevos.domain.repositories.PrecioCostoRepository;
import com.distribuidora.huevos.domain.valueobjects.Precio;
import com.distribuidora.huevos.domain.valueobjects.PrecioCosto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ActualizarPrecioCostoService {

    private final PrecioCostoRepository precioCostoRepository;

    public ActualizarPrecioCostoService(PrecioCostoRepository precioCostoRepository) {
        this.precioCostoRepository = precioCostoRepository;
    }

    public PrecioCostoResponse ejecutar(ActualizarPrecioCostoCommand command) {
        PrecioCosto actual = precioCostoRepository.findCurrent();
        PrecioCosto actualizado = actual.conNuevosCostos(
                Precio.de(command.getCostoExtra()),
                Precio.de(command.getCostoAA()),
                Precio.de(command.getCostoA()),
                Precio.de(command.getCostoB()));
        PrecioCosto guardado = precioCostoRepository.save(actualizado);
        return new PrecioCostoResponse(
                guardado.getCostoExtra().getValor(),
                guardado.getCostoAA().getValor(),
                guardado.getCostoA().getValor(),
                guardado.getCostoB().getValor());
    }
}
