package com.distribuidora.huevos.application.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AbonoResponse {

    private Long id;
    private BigDecimal monto;
    private String medioPago;
    private LocalDateTime fecha;

    public AbonoResponse() {}

    public Long getId()                  { return id; }
    public void setId(Long id)           { this.id = id; }

    public BigDecimal getMonto()                   { return monto; }
    public void setMonto(BigDecimal monto)         { this.monto = monto; }

    public String getMedioPago()                   { return medioPago; }
    public void setMedioPago(String medioPago)     { this.medioPago = medioPago; }

    public LocalDateTime getFecha()                { return fecha; }
    public void setFecha(LocalDateTime fecha)      { this.fecha = fecha; }
}
