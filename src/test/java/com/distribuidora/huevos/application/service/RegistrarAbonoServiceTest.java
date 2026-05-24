package com.distribuidora.huevos.application.service;

import com.distribuidora.huevos.application.dto.command.RegistrarAbonoCommand;
import com.distribuidora.huevos.domain.entities.Caja;
import com.distribuidora.huevos.domain.entities.Cliente;
import com.distribuidora.huevos.domain.entities.Credito;
import com.distribuidora.huevos.domain.enums.TipoCliente;
import com.distribuidora.huevos.domain.enums.TipoPago;
import com.distribuidora.huevos.domain.exceptions.RecursoNoEncontradoException;
import com.distribuidora.huevos.domain.repositories.CajaRepository;
import com.distribuidora.huevos.domain.repositories.CreditoRepository;
import com.distribuidora.huevos.domain.valueobjects.Dinero;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistrarAbonoServiceTest {

    @Mock private CreditoRepository creditoRepository;
    @Mock private CajaRepository cajaRepository;

    @InjectMocks
    private RegistrarAbonoService service;

    private Cliente cliente;
    private Credito credito;

    @BeforeEach
    void setUp() {
        cliente = new Cliente(1L, "Juan", TipoCliente.NORMAL, null, null);
        credito = Credito.nuevo(cliente, Dinero.de(new BigDecimal("100000.00")));
    }

    // ── flujo normal ──────────────────────────────────────────────────────────

    @Test
    void abonoActualizaCreditoYPersiste() {
        when(creditoRepository.findByClienteId(1L)).thenReturn(Optional.of(credito));
        when(cajaRepository.findByFecha(any())).thenReturn(Optional.empty());
        when(cajaRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.ejecutar(crearCommand(1L, "40000.00", TipoPago.EFECTIVO));

        verify(creditoRepository).save(any(Credito.class));
        verify(cajaRepository).save(any(Caja.class));
    }

    // ── contabilidad de caja: regla de negocio crítica ────────────────────────

    @Test
    void abonoEfectivoRefleja_EnEfectivo_YAbonos_SinDobleConteo() {
        // Escenario: abono de S/20,000 en efectivo.
        // Debe sumarse a totalEfectivo (dinero físico) Y a totalAbonos (informativo).
        // calcularTotalCobrado() = efectivo + transferencia → no hay doble conteo.
        when(creditoRepository.findByClienteId(1L)).thenReturn(Optional.of(credito));
        when(cajaRepository.findByFecha(any())).thenReturn(Optional.empty());

        ArgumentCaptor<Caja> cajaCaptor = ArgumentCaptor.forClass(Caja.class);
        when(cajaRepository.save(cajaCaptor.capture())).thenAnswer(inv -> inv.getArgument(0));

        service.ejecutar(crearCommand(1L, "20000.00", TipoPago.EFECTIVO));

        Caja cajaGuardada = cajaCaptor.getValue();
        assertThat(cajaGuardada.getTotalEfectivo().getValor())
                .as("El abono en efectivo debe reflejarse en el cajón de efectivo")
                .isEqualByComparingTo("20000.00");
        assertThat(cajaGuardada.getTotalAbonos().getValor())
                .as("Debe quedar registro de la deuda cobrada hoy")
                .isEqualByComparingTo("20000.00");
        assertThat(cajaGuardada.calcularTotalCobrado().getValor())
                .as("totalCobrado no debe contar los abonos dos veces")
                .isEqualByComparingTo("20000.00");
    }

    @Test
    void abonoTransferenciaRefleja_EnTransferencia_YAbonos() {
        when(creditoRepository.findByClienteId(1L)).thenReturn(Optional.of(credito));
        when(cajaRepository.findByFecha(any())).thenReturn(Optional.empty());

        ArgumentCaptor<Caja> cajaCaptor = ArgumentCaptor.forClass(Caja.class);
        when(cajaRepository.save(cajaCaptor.capture())).thenAnswer(inv -> inv.getArgument(0));

        service.ejecutar(crearCommand(1L, "15000.00", TipoPago.TRANSFERENCIA));

        Caja cajaGuardada = cajaCaptor.getValue();
        assertThat(cajaGuardada.getTotalTransferencia().getValor()).isEqualByComparingTo("15000.00");
        assertThat(cajaGuardada.getTotalAbonos().getValor()).isEqualByComparingTo("15000.00");
        assertThat(cajaGuardada.getTotalEfectivo().getValor()).isEqualByComparingTo("0.00");
    }

    @Test
    void abonoReutilizaCajaExistenteSiHayUnaDiaria() {
        // Hay una caja con S/50,000 en efectivo de ventas del día.
        // El abono de S/20,000 debe sumarle, no crear una caja nueva.
        Caja cajaExistente = Caja.nueva(LocalDate.now());
        cajaExistente.registrarPago(TipoPago.EFECTIVO, Dinero.de(new BigDecimal("50000.00")));

        when(creditoRepository.findByClienteId(1L)).thenReturn(Optional.of(credito));
        when(cajaRepository.findByFecha(any())).thenReturn(Optional.of(cajaExistente));

        ArgumentCaptor<Caja> cajaCaptor = ArgumentCaptor.forClass(Caja.class);
        when(cajaRepository.save(cajaCaptor.capture())).thenAnswer(inv -> inv.getArgument(0));

        service.ejecutar(crearCommand(1L, "20000.00", TipoPago.EFECTIVO));

        Caja cajaGuardada = cajaCaptor.getValue();
        // 50k de ventas + 20k del abono = 70k en efectivo
        assertThat(cajaGuardada.getTotalEfectivo().getValor()).isEqualByComparingTo("70000.00");
        assertThat(cajaGuardada.getTotalAbonos().getValor()).isEqualByComparingTo("20000.00");
    }

    // ── error paths ───────────────────────────────────────────────────────────

    @Test
    void clienteSinCreditoLanzaExcepcion() {
        when(creditoRepository.findByClienteId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.ejecutar(crearCommand(99L, "100.00", TipoPago.EFECTIVO)))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("99");
    }

    @Test
    void abonoMayorAlSaldoLanzaExcepcionDelDominio() {
        // El crédito tiene 100k pero se intenta abonar 999k.
        when(creditoRepository.findByClienteId(1L)).thenReturn(Optional.of(credito));

        assertThatThrownBy(() -> service.ejecutar(crearCommand(1L, "999999.00", TipoPago.EFECTIVO)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("excede el saldo pendiente");
    }

    // ── helper ────────────────────────────────────────────────────────────────

    private RegistrarAbonoCommand crearCommand(Long clienteId, String monto, TipoPago medioPago) {
        RegistrarAbonoCommand command = new RegistrarAbonoCommand();
        command.setClienteId(clienteId);
        command.setMonto(new BigDecimal(monto));
        command.setMedioPago(medioPago);
        return command;
    }
}
