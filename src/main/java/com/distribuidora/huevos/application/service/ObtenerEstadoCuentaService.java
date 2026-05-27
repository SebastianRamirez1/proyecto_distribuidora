package com.distribuidora.huevos.application.service;

import com.distribuidora.huevos.application.dto.response.MovimientoCuentaResponse;
import com.distribuidora.huevos.domain.entities.Abono;
import com.distribuidora.huevos.domain.entities.CargaSaldo;
import com.distribuidora.huevos.domain.entities.Venta;
import com.distribuidora.huevos.domain.exceptions.RecursoNoEncontradoException;
import com.distribuidora.huevos.domain.repositories.AbonoRepository;
import com.distribuidora.huevos.domain.repositories.CargaSaldoRepository;
import com.distribuidora.huevos.domain.repositories.ClienteRepository;
import com.distribuidora.huevos.domain.repositories.VentaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Construye el estado de cuenta completo de un cliente combinando tres fuentes:
 * cargas manuales de saldo (migraciones), ventas al fiado y abonos.
 * El resultado va ordenado del movimiento más reciente al más antiguo.
 */
@Service
@Transactional(readOnly = true)
public class ObtenerEstadoCuentaService {

    private final ClienteRepository    clienteRepository;
    private final CargaSaldoRepository cargaSaldoRepository;
    private final AbonoRepository      abonoRepository;
    private final VentaRepository      ventaRepository;

    public ObtenerEstadoCuentaService(ClienteRepository clienteRepository,
                                       CargaSaldoRepository cargaSaldoRepository,
                                       AbonoRepository abonoRepository,
                                       VentaRepository ventaRepository) {
        this.clienteRepository    = clienteRepository;
        this.cargaSaldoRepository = cargaSaldoRepository;
        this.abonoRepository      = abonoRepository;
        this.ventaRepository      = ventaRepository;
    }

    public List<MovimientoCuentaResponse> ejecutar(Long clienteId) {
        clienteRepository.findById(clienteId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Cliente no encontrado con ID: " + clienteId));

        List<MovimientoCuentaResponse> movimientos = new ArrayList<>();

        // 1. Cargas manuales de saldo (migración de cuaderno)
        for (CargaSaldo cs : cargaSaldoRepository.findByClienteIdOrderByFechaAsc(clienteId)) {
            movimientos.add(new MovimientoCuentaResponse(
                    cs.getFecha(),
                    "MIGRACION",
                    cs.getDescripcion(),
                    cs.getMonto().getValor(),
                    true));
        }

        // 2. Ventas al fiado registradas en el sistema
        for (Venta v : ventaRepository.findFiadasByClienteId(clienteId)) {
            String desc = v.getCantidad().getValor() + " canasta(s) "
                    + v.getTipoProducto().name();
            movimientos.add(new MovimientoCuentaResponse(
                    v.getFecha(),
                    "VENTA_FIADO",
                    desc,
                    v.calcularTotal().getValor(),
                    true));
        }

        // 3. Abonos realizados por el cliente
        for (Abono a : abonoRepository.findByClienteIdOrderByFechaDesc(clienteId)) {
            String desc = "Abono " + a.getMedioPago().name().toLowerCase().replace("_", " ");
            movimientos.add(new MovimientoCuentaResponse(
                    a.getFecha(),
                    "ABONO",
                    desc,
                    a.getMonto().getValor(),
                    false));
        }

        // Ordenar todos los movimientos: más reciente primero
        movimientos.sort((x, y) -> y.getFecha().compareTo(x.getFecha()));

        return movimientos;
    }
}
