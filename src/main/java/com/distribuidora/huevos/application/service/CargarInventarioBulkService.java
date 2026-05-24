package com.distribuidora.huevos.application.service;

import com.distribuidora.huevos.application.dto.command.CargarInventarioBulkCommand;
import com.distribuidora.huevos.application.dto.response.InventarioResponse;
import com.distribuidora.huevos.domain.entities.Inventario;
import com.distribuidora.huevos.domain.enums.TipoProducto;
import com.distribuidora.huevos.domain.repositories.InventarioRepository;
import com.distribuidora.huevos.domain.valueobjects.Cantidad;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Carga todos los tipos de huevo en una sola transacción.
 * Reemplaza las 4 llamadas separadas a CargarInventarioService.
 */
@Service
@Transactional
public class CargarInventarioBulkService {

    private final InventarioRepository inventarioRepository;

    public CargarInventarioBulkService(InventarioRepository inventarioRepository) {
        this.inventarioRepository = inventarioRepository;
    }

    public InventarioResponse ejecutar(CargarInventarioBulkCommand command) {
        Inventario inventario = inventarioRepository.findUnico();

        if (command.getExtra() > 0)
            inventario.agregar(TipoProducto.EXTRA, new Cantidad(command.getExtra()));
        if (command.getAa() > 0)
            inventario.agregar(TipoProducto.AA, new Cantidad(command.getAa()));
        if (command.getA() > 0)
            inventario.agregar(TipoProducto.A, new Cantidad(command.getA()));
        if (command.getB() > 0)
            inventario.agregar(TipoProducto.B, new Cantidad(command.getB()));

        inventarioRepository.save(inventario);

        return new InventarioResponse(
                inventario.getStockExtra(),
                inventario.getStockAA(),
                inventario.getStockA(),
                inventario.getStockB());
    }
}
