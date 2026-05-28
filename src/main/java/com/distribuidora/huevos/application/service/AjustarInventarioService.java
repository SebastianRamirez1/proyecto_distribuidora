package com.distribuidora.huevos.application.service;

import com.distribuidora.huevos.application.dto.command.AjustarInventarioCommand;
import com.distribuidora.huevos.application.dto.response.InventarioResponse;
import com.distribuidora.huevos.domain.entities.Inventario;
import com.distribuidora.huevos.domain.repositories.InventarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AjustarInventarioService {

    private final InventarioRepository inventarioRepository;

    public AjustarInventarioService(InventarioRepository inventarioRepository) {
        this.inventarioRepository = inventarioRepository;
    }

    public InventarioResponse ejecutar(AjustarInventarioCommand command) {
        Inventario inventario = inventarioRepository.findUnico();
        inventario.ajustar(
            command.getStockExtra(),  // double
            command.getStockAA(),     // double
            command.getStockA(),
            command.getStockB()
        );
        inventarioRepository.save(inventario);
        return new InventarioResponse(
            inventario.getStockExtra(),
            inventario.getStockAA(),
            inventario.getStockA(),
            inventario.getStockB()
        );
    }
}
