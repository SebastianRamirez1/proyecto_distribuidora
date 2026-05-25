package com.distribuidora.huevos.infrastructure.persistence.repository.impl;

import com.distribuidora.huevos.domain.entities.Factura;
import com.distribuidora.huevos.domain.repositories.FacturaRepository;
import com.distribuidora.huevos.infrastructure.persistence.entity.FacturaJpaEntity;
import com.distribuidora.huevos.infrastructure.persistence.repository.FacturaJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class FacturaRepositoryImpl implements FacturaRepository {

    private final FacturaJpaRepository jpa;

    public FacturaRepositoryImpl(FacturaJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Factura save(Factura f) {
        return toDomain(jpa.save(toEntity(f)));
    }

    @Override
    public Optional<Factura> findById(Long id) {
        return jpa.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Factura> findByVentaId(Long ventaId) {
        return jpa.findByVentaId(ventaId).map(this::toDomain);
    }

    @Override
    public List<Factura> findAllOrderByFechaDesc() {
        return jpa.findAllOrderByFechaDesc().stream().map(this::toDomain).toList();
    }

    @Override
    public List<Factura> findByClienteIdOrderByFechaDesc(Long clienteId) {
        return jpa.findByClienteIdOrderByFechaDesc(clienteId).stream().map(this::toDomain).toList();
    }

    // ── Mappers ───────────────────────────────────────────────────────────────

    private FacturaJpaEntity toEntity(Factura f) {
        FacturaJpaEntity e = new FacturaJpaEntity();
        e.setId(f.getId());
        e.setNumero(f.getNumero());
        e.setVentaId(f.getVentaId());
        e.setClienteId(f.getClienteId());
        e.setFechaEmision(f.getFechaEmision());
        e.setTipo(f.getTipo());
        e.setEstado(f.getEstado());
        e.setNombreCliente(f.getNombreCliente());
        e.setNitCliente(f.getNitCliente());
        e.setTipoProducto(f.getTipoProducto());
        e.setCantidad(f.getCantidad());
        e.setPrecioUnitario(f.getPrecioUnitario());
        e.setTotal(f.getTotal());
        e.setTipoPago(f.getTipoPago());
        return e;
    }

    private Factura toDomain(FacturaJpaEntity e) {
        return new Factura(
                e.getId(), e.getNumero(), e.getVentaId(), e.getClienteId(),
                e.getFechaEmision(), e.getTipo(), e.getEstado(),
                e.getNombreCliente(), e.getNitCliente(),
                e.getTipoProducto(), e.getCantidad(),
                e.getPrecioUnitario(), e.getTotal(), e.getTipoPago()
        );
    }
}
