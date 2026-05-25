package com.distribuidora.huevos.application.service;

import com.distribuidora.huevos.application.dto.command.RegistrarVentaCommand;
import com.distribuidora.huevos.application.dto.response.VentaResponse;
import com.distribuidora.huevos.application.mapper.VentaMapper;
import com.distribuidora.huevos.domain.entities.*;
import com.distribuidora.huevos.domain.enums.TipoPago;
import com.distribuidora.huevos.domain.exceptions.PrecioInvalidoException;
import com.distribuidora.huevos.domain.exceptions.RecursoNoEncontradoException;
import com.distribuidora.huevos.domain.repositories.*;
import com.distribuidora.huevos.domain.valueobjects.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
    private final PrecioCostoRepository precioCostoRepository;
    private final VentaMapper ventaMapper;

    public RegistrarVentaService(ClienteRepository clienteRepository,
                                 VentaRepository ventaRepository,
                                 InventarioRepository inventarioRepository,
                                 CajaRepository cajaRepository,
                                 CreditoRepository creditoRepository,
                                 PrecioPublicoRepository precioPublicoRepository,
                                 PrecioCostoRepository precioCostoRepository,
                                 VentaMapper ventaMapper) {
        this.clienteRepository = clienteRepository;
        this.ventaRepository = ventaRepository;
        this.inventarioRepository = inventarioRepository;
        this.cajaRepository = cajaRepository;
        this.creditoRepository = creditoRepository;
        this.precioPublicoRepository = precioPublicoRepository;
        this.precioCostoRepository = precioCostoRepository;
        this.ventaMapper = ventaMapper;
    }

    public VentaResponse ejecutar(RegistrarVentaCommand command) {
        Cliente cliente = clienteRepository.findById(command.getClienteId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Cliente no encontrado con ID: " + command.getClienteId()));

        PrecioPublico precioPublico = precioPublicoRepository.findCurrent();
        PrecioCosto precioCosto = precioCostoRepository.findCurrent();
        Cantidad cantidad = new Cantidad(command.getCantidad());

        // Si viene precioManual se aplica directamente (rebaja puntual);
        // de lo contrario se calcula según el perfil del cliente.
        Precio precioUnitario = (command.getPrecioManual() != null)
                ? Precio.de(command.getPrecioManual())
                : cliente.calcularPrecio(command.getTipoProducto(), cantidad, precioPublico);

        // Guardia: precio cero indica que los precios públicos no han sido configurados.
        if (precioUnitario.getValor().compareTo(BigDecimal.ZERO) == 0) {
            throw new PrecioInvalidoException(
                    "El precio para canastas " + command.getTipoProducto().name() +
                    " es S/ 0.00. Configura los precios en el módulo de Precios antes de registrar ventas.");
        }

        // Costo de liquidación vigente al momento de la venta (puede ser 0 si no configurado)
        Precio costoUnitario = precioCosto.obtenerCosto(command.getTipoProducto());

        Inventario inventario = inventarioRepository.findUnico();
        inventario.descontar(command.getTipoProducto(), cantidad);
        inventarioRepository.save(inventario);

        Venta venta = new Venta(null, cliente, command.getTipoProducto(), cantidad,
                precioUnitario, costoUnitario, command.getTipoPago(), LocalDateTime.now());
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
