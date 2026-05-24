package com.distribuidora.huevos.application.service;

import com.distribuidora.huevos.application.dto.response.InventarioResponse;
import com.distribuidora.huevos.domain.entities.Inventario;
import com.distribuidora.huevos.domain.repositories.InventarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ConsultarInventarioService {

    private final InventarioRepository inventarioRepository;

    public ConsultarInventarioService(InventarioRepository inventarioRepository) {
        this.inventarioRepository = inventarioRepository;
    }

    public InventarioResponse ejecutar() {
        Inventario inventario = inventarioRepository.findUnico();
        return new InventarioResponse(inventario.getStockExtra(), inventario.getStockAA(),
                inventario.getStockA(), inventario.getStockB());
    }
}
