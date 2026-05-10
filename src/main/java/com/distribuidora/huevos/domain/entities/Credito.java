package com.distribuidora.huevos.domain.entities;

import com.distribuidora.huevos.domain.valueobjects.Dinero;

public class Credito {

    private Long id;
    private final Cliente cliente;
    private Dinero montoTotal;
    private Dinero montoPagado;

    public Credito(Long id, Cliente cliente, Dinero montoTotal, Dinero montoPagado) {
        this.id = id;
        this.cliente = cliente;
        this.montoTotal = montoTotal;
        this.montoPagado = montoPagado;
    }

    public static Credito nuevo(Cliente cliente, Dinero montoInicial) {
        return new Credito(null, cliente, montoInicial, Dinero.cero());
    }

    public void agregarDeuda(Dinero monto) {
        this.montoTotal = this.montoTotal.sumar(monto);
    }

    public void abonar(Dinero monto) {
        Dinero pendiente = saldoPendiente();
        if (monto.esMayorQue(pendiente)) {
            throw new IllegalArgumentException(
                    String.format("El abono de $%s excede el saldo pendiente de $%s",
                            monto.getValor(), pendiente.getValor()));
        }
        this.montoPagado = this.montoPagado.sumar(monto);
    }

    public Dinero saldoPendiente() {
        return montoTotal.restar(montoPagado);
    }

    public boolean estaSaldado() {
        return saldoPendiente().equals(Dinero.cero());
    }

    public Long getId() {
        return id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public Dinero getMontoTotal() {
        return montoTotal;
    }

    public Dinero getMontoPagado() {
        return montoPagado;
    }
}
