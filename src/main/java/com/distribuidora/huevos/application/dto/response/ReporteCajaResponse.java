package com.distribuidora.huevos.application.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ReporteCajaResponse {

    private LocalDate fecha;
    private BigDecimal totalEfectivo;
    private BigDecimal totalTransferencia;
    private BigDecimal totalFiado;
    private BigDecimal totalAbonos;
    private BigDecimal totalCobrado;

    public ReporteCajaResponse() {}

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public BigDecimal getTotalEfectivo() { return totalEfectivo; }
    public void setTotalEfectivo(BigDecimal totalEfectivo) { this.totalEfectivo = totalEfectivo; }

    public BigDecimal getTotalTransferencia() { return totalTransferencia; }
    public void setTotalTransferencia(BigDecimal totalTransferencia) { this.totalTransferencia = totalTransferencia; }

    public BigDecimal getTotalFiado() { return totalFiado; }
    public void setTotalFiado(BigDecimal totalFiado) { this.totalFiado = totalFiado; }

    public BigDecimal getTotalAbonos() { return totalAbonos; }
    public void setTotalAbonos(BigDecimal totalAbonos) { this.totalAbonos = totalAbonos; }

    public BigDecimal getTotalCobrado() { return totalCobrado; }
    public void setTotalCobrado(BigDecimal totalCobrado) { this.totalCobrado = totalCobrado; }
}
