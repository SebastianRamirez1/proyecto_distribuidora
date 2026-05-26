package com.distribuidora.huevos.application.service;

import com.distribuidora.huevos.domain.entities.*;
import com.distribuidora.huevos.domain.enums.TipoPago;
import com.distribuidora.huevos.domain.exceptions.RecursoNoEncontradoException;
import com.distribuidora.huevos.domain.exceptions.VentaOperacionException;
import com.distribuidora.huevos.domain.repositories.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@Transactional
public class AnularVentaService {

    private final VentaRepository ventaRepository;
    private final InventarioRepository inventarioRepository;
    private final CajaRepository cajaRepository;
    private final CreditoRepository creditoRepository;

    public AnularVentaService(VentaRepository ventaRepository,
                              InventarioRepository inventarioRepository,
                              CajaRepository cajaRepository,
                              CreditoRepository creditoRepository) {
        this.ventaRepository = ventaRepository;
        this.inventarioRepository = inventarioRepository;
        this.cajaRepository = cajaRepository;
        this.creditoRepository = creditoRepository;
    }

    public void ejecutar(Long ventaId) {
        // 1. Buscar la venta
        Venta venta = ventaRepository.findById(ventaId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Venta no encontrada con ID: " + ventaId));

        // 2. Validar que no esté ya anulada
        if (venta.isAnulada()) {
            throw new VentaOperacionException(
                    "La venta #" + ventaId + " ya fue anulada anteriormente.");
        }

        // 3. Marcar como anulada y persistir
        venta.anular();
        ventaRepository.save(venta);

        // 4. Restaurar inventario
        Inventario inventario = inventarioRepository.findUnico();
        inventario.agregar(venta.getTipoProducto(), venta.getCantidad());
        inventarioRepository.save(inventario);

        // 5. Revertir movimiento de caja del día de la venta
        LocalDate fechaVenta = venta.getFecha().toLocalDate();
        cajaRepository.findByFecha(fechaVenta).ifPresent(caja -> {
            caja.revertirPago(venta.getTipoPago(), venta.calcularTotal());
            cajaRepository.save(caja);
        });

        // 6. Si era fiado, revertir deuda del crédito (cliente puede ser null si es público general)
        if (venta.getTipoPago() == TipoPago.FIADO && venta.getCliente() != null) {
            creditoRepository.findByClienteId(venta.getCliente().getId())
                    .ifPresent(credito -> {
                        credito.revertirDeuda(venta.calcularTotal());
                        creditoRepository.save(credito);
                    });
        }
    }
}
