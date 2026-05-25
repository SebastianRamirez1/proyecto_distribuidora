package com.distribuidora.huevos.application.service;

import com.distribuidora.huevos.application.dto.command.ConfigurarFacturaCommand;
import com.distribuidora.huevos.application.dto.response.ConfiguracionFacturaResponse;
import com.distribuidora.huevos.domain.entities.ConfiguracionFactura;
import com.distribuidora.huevos.domain.repositories.ConfiguracionFacturaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ActualizarConfiguracionFacturaService {

    private final ConfiguracionFacturaRepository repo;
    private final ObtenerConfiguracionFacturaService obtener;

    public ActualizarConfiguracionFacturaService(ConfiguracionFacturaRepository repo,
                                                  ObtenerConfiguracionFacturaService obtener) {
        this.repo = repo;
        this.obtener = obtener;
    }

    public ConfiguracionFacturaResponse ejecutar(ConfigurarFacturaCommand cmd) {
        ConfiguracionFactura cfg = repo.findUnica();

        cfg.setRazonSocial(cmd.getRazonSocial());
        cfg.setNit(cmd.getNit());
        cfg.setDireccion(cmd.getDireccion() != null ? cmd.getDireccion() : "");
        cfg.setCiudad(cmd.getCiudad() != null ? cmd.getCiudad() : "");
        cfg.setTelefono(cmd.getTelefono() != null ? cmd.getTelefono() : "");
        cfg.setRegimen(cmd.getRegimen() != null ? cmd.getRegimen() : "No responsable de IVA");
        cfg.setResolucionNumero(cmd.getResolucionNumero() != null ? cmd.getResolucionNumero() : "");
        cfg.setResolucionFecha(cmd.getResolucionFecha());
        cfg.setResolucionPrefijo(cmd.getResolucionPrefijo() != null ? cmd.getResolucionPrefijo() : "FAC");
        cfg.setResolucionDesde(cmd.getResolucionDesde() != null ? cmd.getResolucionDesde() : 1);
        cfg.setResolucionHasta(cmd.getResolucionHasta() != null ? cmd.getResolucionHasta() : 9999);

        repo.save(cfg);
        return obtener.ejecutar();
    }
}
