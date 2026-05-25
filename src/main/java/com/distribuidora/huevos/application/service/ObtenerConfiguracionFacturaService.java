package com.distribuidora.huevos.application.service;

import com.distribuidora.huevos.application.dto.response.ConfiguracionFacturaResponse;
import com.distribuidora.huevos.domain.entities.ConfiguracionFactura;
import com.distribuidora.huevos.domain.repositories.ConfiguracionFacturaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ObtenerConfiguracionFacturaService {

    private final ConfiguracionFacturaRepository repo;

    public ObtenerConfiguracionFacturaService(ConfiguracionFacturaRepository repo) {
        this.repo = repo;
    }

    public ConfiguracionFacturaResponse ejecutar() {
        ConfiguracionFactura cfg = repo.findUnica();
        ConfiguracionFacturaResponse r = new ConfiguracionFacturaResponse();
        r.setRazonSocial(cfg.getRazonSocial());
        r.setNit(cfg.getNit());
        r.setDireccion(cfg.getDireccion());
        r.setCiudad(cfg.getCiudad());
        r.setTelefono(cfg.getTelefono());
        r.setRegimen(cfg.getRegimen());
        r.setResolucionNumero(cfg.getResolucionNumero());
        r.setResolucionFecha(cfg.getResolucionFecha());
        r.setResolucionPrefijo(cfg.getResolucionPrefijo());
        r.setResolucionDesde(cfg.getResolucionDesde());
        r.setResolucionHasta(cfg.getResolucionHasta());
        r.setConsecutivoActual(cfg.getConsecutivoActual());
        r.setConfigurada(cfg.estaConfigurada());
        return r;
    }
}
