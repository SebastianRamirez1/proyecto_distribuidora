package com.distribuidora.huevos.application.service;

import com.distribuidora.huevos.application.dto.command.GenerarFacturaCommand;
import com.distribuidora.huevos.application.dto.response.FacturaResponse;
import com.distribuidora.huevos.domain.entities.ConfiguracionFactura;
import com.distribuidora.huevos.domain.entities.Factura;
import com.distribuidora.huevos.domain.entities.Venta;
import com.distribuidora.huevos.domain.enums.EstadoFactura;
import com.distribuidora.huevos.domain.exceptions.RecursoNoEncontradoException;
import com.distribuidora.huevos.domain.repositories.ConfiguracionFacturaRepository;
import com.distribuidora.huevos.domain.repositories.FacturaRepository;
import com.distribuidora.huevos.domain.repositories.VentaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class GenerarFacturaService {

    private final VentaRepository ventaRepository;
    private final FacturaRepository facturaRepository;
    private final ConfiguracionFacturaRepository configRepo;

    public GenerarFacturaService(VentaRepository ventaRepository,
                                  FacturaRepository facturaRepository,
                                  ConfiguracionFacturaRepository configRepo) {
        this.ventaRepository = ventaRepository;
        this.facturaRepository = facturaRepository;
        this.configRepo = configRepo;
    }

    public FacturaResponse ejecutar(GenerarFacturaCommand cmd) {
        // 1. Verificar si ya existe factura para esta venta
        facturaRepository.findByVentaId(cmd.getVentaId()).ifPresent(f -> {
            throw new IllegalStateException(
                    "La venta #" + cmd.getVentaId() + " ya tiene la factura " + f.getNumero());
        });

        // 2. Cargar la venta
        Venta venta = ventaRepository.findById(cmd.getVentaId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Venta no encontrada con ID: " + cmd.getVentaId()));

        if (venta.isAnulada()) {
            throw new IllegalStateException("No se puede facturar una venta anulada.");
        }

        // 3. Obtener y actualizar el consecutivo de forma atómica
        ConfiguracionFactura cfg = configRepo.findUnica();
        String numero = cfg.generarYAvanzarConsecutivo();
        configRepo.save(cfg);

        // 4. Determinar NIT del cliente
        String nitCliente = (cmd.getNitCliente() != null && !cmd.getNitCliente().isBlank())
                ? cmd.getNitCliente()
                : "Sin NIT";

        // 5. Construir y persistir la factura
        Factura factura = new Factura(
                null,
                numero,
                venta.getId(),
                venta.getCliente().getId(),
                LocalDateTime.now(),
                cmd.getTipo(),
                EstadoFactura.EMITIDA,
                venta.getCliente().getNombre(),
                nitCliente,
                venta.getTipoProducto(),
                venta.getCantidad().getValor(),
                venta.getPrecioUnitario().getValor(),
                venta.calcularTotal().getValor(),
                venta.getTipoPago()
        );

        Factura guardada = facturaRepository.save(factura);
        return toResponse(guardada);
    }

    private FacturaResponse toResponse(Factura f) {
        FacturaResponse r = new FacturaResponse();
        r.setId(f.getId());
        r.setNumero(f.getNumero());
        r.setVentaId(f.getVentaId());
        r.setClienteId(f.getClienteId());
        r.setNombreCliente(f.getNombreCliente());
        r.setNitCliente(f.getNitCliente());
        r.setFechaEmision(f.getFechaEmision());
        r.setTipo(f.getTipo().name());
        r.setEstado(f.getEstado().name());
        r.setTipoProducto(f.getTipoProducto().name());
        r.setCantidad(f.getCantidad());
        r.setPrecioUnitario(f.getPrecioUnitario());
        r.setTotal(f.getTotal());
        r.setTipoPago(f.getTipoPago().name());
        return r;
    }
}
