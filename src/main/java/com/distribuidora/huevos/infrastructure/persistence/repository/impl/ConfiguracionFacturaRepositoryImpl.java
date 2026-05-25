package com.distribuidora.huevos.infrastructure.persistence.repository.impl;

import com.distribuidora.huevos.domain.entities.ConfiguracionFactura;
import com.distribuidora.huevos.domain.repositories.ConfiguracionFacturaRepository;
import com.distribuidora.huevos.infrastructure.persistence.entity.ConfiguracionFacturaJpaEntity;
import com.distribuidora.huevos.infrastructure.persistence.repository.ConfiguracionFacturaJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public class ConfiguracionFacturaRepositoryImpl implements ConfiguracionFacturaRepository {

    private final ConfiguracionFacturaJpaRepository jpa;

    public ConfiguracionFacturaRepositoryImpl(ConfiguracionFacturaJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public ConfiguracionFactura findUnica() {
        ConfiguracionFacturaJpaEntity e = jpa.findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "No existe configuración de factura. Configúrala desde la sección Facturas."));
        return toDomain(e);
    }

    @Override
    public ConfiguracionFactura findUnicaParaActualizar() {
        ConfiguracionFacturaJpaEntity e = jpa.findFirstForUpdate()
                .orElseThrow(() -> new IllegalStateException(
                        "No existe configuración de factura. Configúrala desde la sección Facturas."));
        return toDomain(e);
    }

    @Override
    public ConfiguracionFactura save(ConfiguracionFactura cfg) {
        ConfiguracionFacturaJpaEntity e = jpa.findFirst().orElse(new ConfiguracionFacturaJpaEntity());
        e.setRazonSocial(cfg.getRazonSocial());
        e.setNit(cfg.getNit());
        e.setDireccion(cfg.getDireccion());
        e.setCiudad(cfg.getCiudad());
        e.setTelefono(cfg.getTelefono());
        e.setRegimen(cfg.getRegimen());
        e.setResolucionNumero(cfg.getResolucionNumero());
        e.setResolucionFecha(cfg.getResolucionFecha());
        e.setResolucionPrefijo(cfg.getResolucionPrefijo());
        e.setResolucionDesde(cfg.getResolucionDesde());
        e.setResolucionHasta(cfg.getResolucionHasta());
        e.setConsecutivoActual(cfg.getConsecutivoActual());
        jpa.save(e);
        return toDomain(e);
    }

    private ConfiguracionFactura toDomain(ConfiguracionFacturaJpaEntity e) {
        return new ConfiguracionFactura(
                e.getId(), e.getRazonSocial(), e.getNit(),
                e.getDireccion(), e.getCiudad(), e.getTelefono(),
                e.getRegimen(), e.getResolucionNumero(), e.getResolucionFecha(),
                e.getResolucionPrefijo(), e.getResolucionDesde(),
                e.getResolucionHasta(), e.getConsecutivoActual()
        );
    }
}
