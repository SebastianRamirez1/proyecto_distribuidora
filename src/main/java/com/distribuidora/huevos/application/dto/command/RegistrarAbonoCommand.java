package com.distribuidora.huevos.application.dto.command;

import com.distribuidora.huevos.domain.enums.TipoPago;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class RegistrarAbonoCommand {

    @NotNull(message = "El ID del cliente es obligatorio")
    private Long clienteId;

    @NotNull(message = "El monto del abono es obligatorio")
    @DecimalMin(value = "0.01", message = "El abono debe ser mayor a 0")
    private BigDecimal monto;

    /** Cómo pagó el cliente: EFECTIVO o TRANSFERENCIA. */
    @NotNull(message = "El medio de pago del abono es obligatorio")
    private TipoPago medioPago;

    public RegistrarAbonoCommand() {}

    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }

    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }

    public TipoPago getMedioPago() { return medioPago; }
    public void setMedioPago(TipoPago medioPago) { this.medioPago = medioPago; }
}
