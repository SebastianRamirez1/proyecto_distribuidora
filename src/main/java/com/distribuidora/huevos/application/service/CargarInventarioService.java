package com.distribuidora.huevos.application.service;

import com.distribuidora.huevos.application.dto.command.CargarInventarioCommand;
import com.distribuidora.huevos.application.dto.response.InventarioResponse;
import com.distribuidora.huevos.domain.entities.Inventario;
import com.distribuidora.huevos.domain.repositories.InventarioRepository;
import com.distribuidora.huevos.domain.valueobjects.Cantidad;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CargarInventarioService {

    private final InventarioRepository inventarioRepository;

    public CargarInventarioService(InventarioRepository inventarioRepository) {
        this.inventarioRepository = inventarioRepository;
    }

    public InventarioResponse ejecutar(CargarInventarioCommand command) {
        Inventario inventario = inventarioRepository.findUnico();
        inventario.agregar(command.getTipoProducto(), new Cantidad(command.getCantidad()));
        inventarioRepository.save(inventario);
        return new InventarioResponse(inventario.getStockExtra(), inventario.getStockAA(),
                inventario.getStockA(), inventario.getStockB());
    }
}
