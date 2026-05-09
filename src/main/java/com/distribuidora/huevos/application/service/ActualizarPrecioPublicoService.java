package com.distribuidora.huevos.application.service;

import com.distribuidora.huevos.application.dto.command.ActualizarPrecioPublicoCommand;
import com.distribuidora.huevos.domain.repositories.PrecioPublicoRepository;
import com.distribuidora.huevos.domain.valueobjects.Precio;
import com.distribuidora.huevos.domain.valueobjects.PrecioPublico;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ActualizarPrecioPublicoService {

    private final PrecioPublicoRepository precioPublicoRepository;

    public ActualizarPrecioPublicoService(PrecioPublicoRepository precioPublicoRepository) {
        this.precioPublicoRepository = precioPublicoRepository;
    }

    public void ejecutar(ActualizarPrecioPublicoCommand command) {
        PrecioPublico actual = precioPublicoRepository.findCurrent();
        PrecioPublico actualizado = actual.conNuevosPrecios(
                Precio.de(command.getPrecioExtra()),
                Precio.de(command.getPrecioNormal()));
        precioPublicoRepository.save(actualizado);
    }
}
