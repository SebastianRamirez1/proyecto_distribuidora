package com.distribuidora.huevos.application.service;

import com.distribuidora.huevos.application.dto.command.CargarSaldoCommand;
import com.distribuidora.huevos.domain.entities.Cliente;
import com.distribuidora.huevos.domain.entities.CargaSaldo;
import com.distribuidora.huevos.domain.entities.Credito;
import com.distribuidora.huevos.domain.enums.TipoCliente;
import com.distribuidora.huevos.domain.exceptions.RecursoNoEncontradoException;
import com.distribuidora.huevos.domain.repositories.CargaSaldoRepository;
import com.distribuidora.huevos.domain.repositories.ClienteRepository;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CargarSaldoServiceTest {

    @Mock private ClienteRepository    clienteRepository;
    @Mock private CreditoRepository    creditoRepository;
    @Mock private CargaSaldoRepository cargaSaldoRepository;

    @InjectMocks
    private CargarSaldoService service;

    private Cliente cliente;

    @BeforeEach
    void setUp() {
        cliente = new Cliente(1L, "Tienda La Esperanza", TipoCliente.NORMAL, null, null);
    }

    @Test
    void cargarSaldoAgregaDeudaACreditoExistente() {
        // El cliente ya tenía $20.000 de deuda; al cargar $15.000 debe quedar $35.000
        Credito creditoExistente = Credito.nuevo(cliente, Dinero.de(new BigDecimal("20000")));
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(creditoRepository.findByClienteId(1L)).thenReturn(Optional.of(creditoExistente));
        when(creditoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(cargaSaldoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.ejecutar(1L, cmd(new BigDecimal("15000"), null));

        ArgumentCaptor<Credito> credCaptor = ArgumentCaptor.forClass(Credito.class);
        verify(creditoRepository).save(credCaptor.capture());
        assertThat(credCaptor.getValue().getMontoTotal().getValor())
                .isEqualByComparingTo("35000");
    }

    @Test
    void cargarSaldoCreaCreditoNuevoSiNoExiste() {
        // El cliente no tiene registro de crédito aún → debe crearse con el monto cargado
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(creditoRepository.findByClienteId(1L)).thenReturn(Optional.empty());
        when(creditoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(cargaSaldoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.ejecutar(1L, cmd(new BigDecimal("45000"), "Deuda cuaderno"));

        ArgumentCaptor<Credito> credCaptor = ArgumentCaptor.forClass(Credito.class);
        verify(creditoRepository).save(credCaptor.capture());
        assertThat(credCaptor.getValue().getMontoTotal().getValor())
                .isEqualByComparingTo("45000");
    }

    @Test
    void cargarSaldoRegistraEntradaDeMigracionConDescripcion() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(creditoRepository.findByClienteId(1L)).thenReturn(Optional.empty());
        when(creditoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(cargaSaldoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.ejecutar(1L, cmd(new BigDecimal("30000"), "Deuda cuaderno al 26/05/2025"));

        ArgumentCaptor<CargaSaldo> csCaptor = ArgumentCaptor.forClass(CargaSaldo.class);
        verify(cargaSaldoRepository).save(csCaptor.capture());
        assertThat(csCaptor.getValue().getDescripcion()).isEqualTo("Deuda cuaderno al 26/05/2025");
        assertThat(csCaptor.getValue().getMonto().getValor()).isEqualByComparingTo("30000");
        assertThat(csCaptor.getValue().getClienteId()).isEqualTo(1L);
    }

    @Test
    void sinDescripcionUsaTextoporDefecto() {
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(creditoRepository.findByClienteId(1L)).thenReturn(Optional.empty());
        when(creditoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(cargaSaldoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.ejecutar(1L, cmd(new BigDecimal("10000"), null));

        ArgumentCaptor<CargaSaldo> csCaptor = ArgumentCaptor.forClass(CargaSaldo.class);
        verify(cargaSaldoRepository).save(csCaptor.capture());
        assertThat(csCaptor.getValue().getDescripcion()).isEqualTo("Saldo migrado de cuaderno");
    }

    @Test
    void clienteNoEncontradoLanzaRecursoNoEncontrado() {
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.ejecutar(99L, cmd(new BigDecimal("10000"), null)))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("99");

        verify(creditoRepository,    never()).save(any());
        verify(cargaSaldoRepository, never()).save(any());
    }

    // ── helper ────────────────────────────────────────────────────────────────

    private CargarSaldoCommand cmd(BigDecimal monto, String descripcion) {
        CargarSaldoCommand c = new CargarSaldoCommand();
        c.setMonto(monto);
        c.setDescripcion(descripcion);
        return c;
    }
}
