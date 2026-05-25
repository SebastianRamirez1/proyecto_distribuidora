package com.distribuidora.huevos.application.service;

import com.distribuidora.huevos.application.dto.response.FacturaResponse;
import com.distribuidora.huevos.domain.entities.Factura;
import com.distribuidora.huevos.domain.repositories.FacturaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ListarFacturasService {

    private final FacturaRepository facturaRepository;

    public ListarFacturasService(FacturaRepository facturaRepository) {
        this.facturaRepository = facturaRepository;
    }

    public List<FacturaResponse> ejecutar() {
        return facturaRepository.findAllOrderByFechaDesc()
                .stream().map(this::toResponse).toList();
    }

    public List<FacturaResponse> ejecutarPorCliente(Long clienteId) {
        return facturaRepository.findByClienteIdOrderByFechaDesc(clienteId)
                .stream().map(this::toResponse).toList();
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
