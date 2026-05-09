package com.distribuidora.huevos.application.service;

import com.distribuidora.huevos.application.dto.command.RegistrarVentaCommand;
import com.distribuidora.huevos.application.dto.response.VentaResponse;
import com.distribuidora.huevos.application.mapper.VentaMapper;
import com.distribuidora.huevos.domain.entities.*;
import com.distribuidora.huevos.domain.enums.TipoPago;
import com.distribuidora.huevos.domain.exceptions.RecursoNoEncontradoException;
import com.distribuidora.huevos.domain.repositories.*;
import com.distribuidora.huevos.domain.valueobjects.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@Transactional
public class RegistrarVentaService {

    private final ClienteRepository clienteRepository;
    private final VentaRepository ventaRepository;
    private final InventarioRepository inventarioRepository;
    private final CajaRepository cajaRepository;
    private final CreditoRepository creditoRepository;
    private final PrecioPublicoRepository precioPublicoRepository;
    private final VentaMapper ventaMapper;

    public RegistrarVentaService(ClienteRepository clienteRepository,
                                 VentaRepository ventaRepository,
                                 InventarioRepository inventarioRepository,
                                 CajaRepository cajaRepository,
                                 CreditoRepository creditoRepository,
                                 PrecioPublicoRepository precioPublicoRepository,
                                 VentaMapper ventaMapper) {
        this.clienteRepository = clienteRepository;
        this.ventaRepository = ventaRepository;
        this.inventarioRepository = inventarioRepository;
        this.cajaRepository = cajaRepository;
        this.creditoRepository = creditoRepository;
        this.precioPublicoRepository = precioPublicoRepository;
        this.ventaMapper = ventaMapper;
    }

    public VentaResponse ejecutar(RegistrarVentaCommand command) {
        Cliente cliente = clienteRepository.findById(command.getClienteId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Cliente no encontrado con ID: " + command.getClienteId()));

        PrecioPublico precioPublico = precioPublicoRepository.findCurrent();
        Cantidad cantidad = new Cantidad(command.getCantidad());

        Precio precioUnitario = cliente.calcularPrecio(
                command.getTipoProducto(), cantidad, precioPublico);

        Inventario inventario = inventarioRepository.findUnico();
        inventario.descontar(command.getTipoProducto(), cantidad);
        inventarioRepository.save(inventario);

        Venta venta = new Venta(null, cliente, command.getTipoProducto(), cantidad,
                precioUnitario, command.getTipoPago(), LocalDateTime.now());
        venta = ventaRepository.save(venta);

        registrarEnCaja(venta);

        if (command.getTipoPago() == TipoPago.FIADO) {
            registrarCredito(cliente, venta.calcularTotal());
        }

        return ventaMapper.toResponse(venta);
    }

    private void registrarEnCaja(Venta venta) {
        LocalDate hoy = venta.getFecha().toLocalDate();
        Caja caja = cajaRepository.findByFecha(hoy).orElse(Caja.nueva(hoy));
        caja.registrarPago(venta.getTipoPago(), venta.calcularTotal());
        cajaRepository.save(caja);
    }

    private void registrarCredito(Cliente cliente, Dinero monto) {
        Credito credito = creditoRepository.findByClienteId(cliente.getId())
                .orElse(Credito.nuevo(cliente, Dinero.cero()));
        credito.agregarDeuda(monto);
        creditoRepository.save(credito);
    }
}
