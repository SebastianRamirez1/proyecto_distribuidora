package com.distribuidora.huevos.domain.entities;

import com.distribuidora.huevos.domain.enums.TipoCliente;
import com.distribuidora.huevos.domain.enums.TipoProducto;
import com.distribuidora.huevos.domain.exceptions.ClienteIncompletoException;
import com.distribuidora.huevos.domain.valueobjects.*;

import java.util.Objects;

public class Cliente {

    private final Long id;
    private final String nombre;
    private final TipoCliente tipo;
    private final PrecioEspecial precioEspecial;
    private final DescuentoPorVolumen descuentoVolumen;

    public Cliente(Long id, String nombre, TipoCliente tipo,
                   PrecioEspecial precioEspecial,
                   DescuentoPorVolumen descuentoVolumen) {
        Objects.requireNonNull(nombre, "El nombre del cliente no puede ser null");
        Objects.requireNonNull(tipo, "El tipo de cliente no puede ser null");
        if (nombre.isBlank()) {
            throw new ClienteIncompletoException("El nombre del cliente no puede estar vacío");
        }
        if (tipo == TipoCliente.ESPECIAL && precioEspecial == null) {
            throw new ClienteIncompletoException(
                    "Un cliente ESPECIAL debe tener precio especial definido. Cliente: " + nombre);
        }
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.precioEspecial = precioEspecial;
        this.descuentoVolumen = descuentoVolumen;
    }

    public Precio calcularPrecio(TipoProducto tipoProducto, Cantidad cantidad,
                                 PrecioPublico precioPublico) {
        if (this.tipo == TipoCliente.NORMAL) {
            return precioPublico.obtenerPrecio(tipoProducto);
        }
        if (this.descuentoVolumen != null && this.descuentoVolumen.aplica(cantidad)) {
            return this.descuentoVolumen.obtenerPrecio(tipoProducto);
        }
        return this.precioEspecial.obtenerPrecio(tipoProducto);
    }

    public Cliente conPrecioEspecial(PrecioEspecial nuevoPrecio) {
        return new Cliente(this.id, this.nombre, this.tipo, nuevoPrecio, this.descuentoVolumen);
    }

    public Cliente conDescuentoVolumen(DescuentoPorVolumen nuevoDescuento) {
        return new Cliente(this.id, this.nombre, this.tipo, this.precioEspecial, nuevoDescuento);
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public TipoCliente getTipo() {
        return tipo;
    }

    public PrecioEspecial getPrecioEspecial() {
        return precioEspecial;
    }

    public DescuentoPorVolumen getDescuentoVolumen() {
        return descuentoVolumen;
    }

    public boolean esEspecial() {
        return this.tipo == TipoCliente.ESPECIAL;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cliente)) return false;
        Cliente cliente = (Cliente) o;
        return Objects.equals(id, cliente.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Cliente{id=" + id + ", nombre='" + nombre + "', tipo=" + tipo + "}";
    }
}
