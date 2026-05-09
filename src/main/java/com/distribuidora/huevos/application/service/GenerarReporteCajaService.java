package com.distribuidora.huevos.application.service;

import com.distribuidora.huevos.application.dto.response.ReporteCajaResponse;
import com.distribuidora.huevos.domain.entities.Caja;
import com.distribuidora.huevos.domain.repositories.CajaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@Transactional(readOnly = true)
public class GenerarReporteCajaService {

    private final CajaRepository cajaRepository;

    public GenerarReporteCajaService(CajaRepository cajaRepository) {
        this.cajaRepository = cajaRepository;
    }

    public ReporteCajaResponse ejecutar(LocalDate fecha) {
        return cajaRepository.findByFecha(fecha)
                .map(this::toResponse)
                .orElse(cajaVacia(fecha));
    }

    private ReporteCajaResponse toResponse(Caja caja) {
        ReporteCajaResponse response = new ReporteCajaResponse();
        response.setFecha(caja.getFecha());
        response.setTotalEfectivo(caja.getTotalEfectivo().getValor());
        response.setTotalTransferencia(caja.getTotalTransferencia().getValor());
        response.setTotalFiado(caja.getTotalFiado().getValor());
        response.setTotalAbonos(caja.getTotalAbonos().getValor());
        response.setTotalCobrado(caja.calcularTotalCobrado().getValor());
        return response;
    }

    private ReporteCajaResponse cajaVacia(LocalDate fecha) {
        ReporteCajaResponse response = new ReporteCajaResponse();
        response.setFecha(fecha);
        response.setTotalEfectivo(BigDecimal.ZERO);
        response.setTotalTransferencia(BigDecimal.ZERO);
        response.setTotalFiado(BigDecimal.ZERO);
        response.setTotalAbonos(BigDecimal.ZERO);
        response.setTotalCobrado(BigDecimal.ZERO);
        return response;
    }
}
