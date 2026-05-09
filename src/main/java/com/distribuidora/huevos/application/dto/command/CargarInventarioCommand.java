package com.distribuidora.huevos.application.dto.command;

import com.distribuidora.huevos.domain.enums.TipoProducto;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class CargarInventarioCommand {

    @NotNull(message = "El tipo de producto es obligatorio")
    private TipoProducto tipoProducto;

    @Min(value = 1, message = "La cantidad a cargar debe ser mayor a 0")
    private int cantidad;

    public CargarInventarioCommand() {}

    public TipoProducto getTipoProducto() { return tipoProducto; }
    public void setTipoProducto(TipoProducto tipoProducto) { this.tipoProducto = tipoProducto; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
}
