package com.distribuidora.huevos.application.service;

import com.distribuidora.huevos.domain.entities.Cliente;
import com.distribuidora.huevos.domain.enums.TipoCliente;
import com.distribuidora.huevos.domain.exceptions.RecursoNoEncontradoException;
import com.distribuidora.huevos.domain.repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EliminarClienteServiceTest {

    @Mock private ClienteRepository    clienteRepository;
    @Mock private FacturaRepository    facturaRepository;
    @Mock private VentaRepository      ventaRepository;
    @Mock private AbonoRepository      abonoRepository;
    @Mock private CargaSaldoRepository cargaSaldoRepository;
    @Mock private CreditoRepository    creditoRepository;

    @InjectMocks
    private EliminarClienteService service;

    private Cliente cliente;

    @BeforeEach
    void setUp() {
        cliente = new Cliente(5L, "Tienda Central", TipoCliente.NORMAL, null, null);
    }

    @Test
    void eliminaClienteExistenteCorrectamente() {
        when(clienteRepository.findById(5L)).thenReturn(Optional.of(cliente));

        assertThatNoException().isThrownBy(() -> service.ejecutar(5L));

        verify(clienteRepository).deleteById(5L);
    }

    @Test
    void respetaOrdenDeEliminacionParaEvitarViolacionesDeFk() {
        // El orden correcto es: facturas → ventas → abonos → carga_saldo → crédito → cliente.
        // Si se invierte, PostgreSQL lanzaría una violación de FK en producción.
        when(clienteRepository.findById(5L)).thenReturn(Optional.of(cliente));

        service.ejecutar(5L);

        InOrder orden = inOrder(
                facturaRepository,
                ventaRepository,
                abonoRepository,
                cargaSaldoRepository,
                creditoRepository,
                clienteRepository
        );
        orden.verify(facturaRepository).deleteByClienteId(5L);
        orden.verify(ventaRepository).deleteByClienteId(5L);
        orden.verify(abonoRepository).deleteByClienteId(5L);
        orden.verify(cargaSaldoRepository).deleteByClienteId(5L);
        orden.verify(creditoRepository).deleteByClienteId(5L);
        orden.verify(clienteRepository).deleteById(5L);
    }

    @Test
    void eliminaTodosLosRegistrosDependientes() {
        when(clienteRepository.findById(5L)).thenReturn(Optional.of(cliente));

        service.ejecutar(5L);

        // Todos los repositorios dependientes reciben su llamada de borrado
        verify(facturaRepository).deleteByClienteId(5L);
        verify(ventaRepository).deleteByClienteId(5L);
        verify(abonoRepository).deleteByClienteId(5L);
        verify(cargaSaldoRepository).deleteByClienteId(5L);
        verify(creditoRepository).deleteByClienteId(5L);
    }

    @Test
    void clienteNoEncontradoLanzaRecursoNoEncontrado() {
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.ejecutar(99L))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("99");
    }

    @Test
    void clienteNoEncontradoNoEliminaNada() {
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.ejecutar(99L))
                .isInstanceOf(RecursoNoEncontradoException.class);

        verify(facturaRepository,     never()).deleteByClienteId(any());
        verify(ventaRepository,       never()).deleteByClienteId(any());
        verify(abonoRepository,       never()).deleteByClienteId(any());
        verify(cargaSaldoRepository,  never()).deleteByClienteId(any());
        verify(creditoRepository,     never()).deleteByClienteId(any());
        verify(clienteRepository,     never()).deleteById(any());
    }
}
